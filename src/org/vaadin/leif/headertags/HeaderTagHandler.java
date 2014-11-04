package org.vaadin.leif.headertags;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import com.vaadin.server.BootstrapFragmentResponse;
import com.vaadin.server.BootstrapListener;
import com.vaadin.server.BootstrapPageResponse;
import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.UI;

public class HeaderTagHandler implements BootstrapListener {

    @Override
    public void modifyBootstrapFragment(BootstrapFragmentResponse response) {
        // Nothing to do here
    }

    @Override
    public void modifyBootstrapPage(BootstrapPageResponse response) {
        Class<? extends UI> uiClass = response.getUiClass();

        /*
         * Append to a fake head tag so we can distinguish between original and
         * new head tags.
         */
        Element fakeHead = new Element(Tag.valueOf("head"), response
                .getDocument().baseUri());

        appendHeadTagAnnotations(fakeHead, uiClass);

        Element realHead = response.getDocument().head();

        // Replace some specific elements into specific location
        replaceIfPresent("meta[http-equiv=Content-Type]", realHead, fakeHead);
        replaceIfPresent("meta[http-equiv=X-UA-Compatible]", realHead, fakeHead);
        replaceIfPresent("link[rel=shortcut icon]", realHead, fakeHead);
        replaceIfPresent("link[rel=icon]", realHead, fakeHead);

        // Dump remaining elements to the end of the head tag
        for (Element element : fakeHead.children()) {
            realHead.appendChild(element);
        }
    }

    private void replaceIfPresent(String selector, Element realHead,
            Element fakeHead) {
        Elements newElements = fakeHead.select(selector);
        if (!newElements.isEmpty()) {
            Elements oldElements = realHead.select(selector);
            if (!oldElements.isEmpty()) {
                for (Element element : newElements) {
                    oldElements.first().before(element);
                }
                oldElements.remove();
            }
        }
    }

    private void appendHeadTagAnnotations(Element head,
            Class<? extends UI> uiClass) {
        for (Annotation annotation : uiClass.getAnnotations()) {
            checkHeadTagAnnotation(head, annotation);
        }
    }

    private void checkHeadTagAnnotation(Element head, Annotation annotation) {
        // Check if a path to a HeadTag meta annotation can be found
        List<Annotation> headTagPath = findHeadTagPath(annotation);
        if (headTagPath != null) {
            appendHeadTag(head, headTagPath);
        } else {
            // Check for an array value annotation
            try {
                Method method = annotation.annotationType().getMethod("value");
                Class<?> type = method.getReturnType();
                if (isAnnotationArray(type)) {
                    Object array = method.invoke(annotation);
                    int length = Array.getLength(array);
                    for (int i = 0; i < length; i++) {
                        Annotation member = (Annotation) Array.get(array, i);
                        checkHeadTagAnnotation(head, member);
                    }
                }
            } catch (NoSuchMethodException e) {
                // Ignore, this is simply not a tag collection annotation
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

    private static boolean isAnnotationArray(Class<?> type) {
        return type.isArray()
                && Annotation.class.isAssignableFrom(type.getComponentType());
    }

    private void appendHeadTag(Element head, List<Annotation> headTagPath) {
        // Should be at least the meta annotation and a "normal" annotation
        assert headTagPath.size() > 1;

        // The first annotation in the path just defines the tag name
        HeadTag tag = (HeadTag) headTagPath.get(0);
        Element element = head.appendElement(tag.value());

        // Then iterate the rest of the path to find attribute values
        for (int i = 1; i < headTagPath.size(); i++) {
            Annotation attribAnnotation = headTagPath.get(i);

            for (Method method : attribAnnotation.annotationType()
                    .getDeclaredMethods()) {
                String name = getHeadTagAttributeName(method);

                try {
                    String value = (String) method.invoke(attribAnnotation);

                    if (HeadTag.NULL_VALUE.equals(value)) {
                        element.removeAttr(name);
                    } else {
                        element.attr(name, value);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(
                            "Error processing @HeadTag annotation method "
                                    + method.getDeclaringClass().getName()
                                    + "." + method.getName(), e);
                }
            }
        }
    }

    private String getHeadTagAttributeName(Method method) {
        HeadTagAttribute headTagAttribute = method
                .getAnnotation(HeadTagAttribute.class);
        if (headTagAttribute != null) {
            return headTagAttribute.value();
        } else {
            // camelCase -> camel-case
            StringBuilder b = new StringBuilder();
            String methodName = method.getName();
            for (int j = 0; j < methodName.length(); j++) {
                char c = methodName.charAt(j);
                if (Character.isUpperCase(c)) {
                    b.append('-');
                    b.append(Character.toLowerCase(c));
                } else {
                    b.append(c);
                }
            }
            return b.toString();
        }
    }

    private List<Annotation> findHeadTagPath(Annotation annotation) {
        // Recursively build a path to a @HeadTag

        // Terminate recursion when reaching a @HeadTag
        if (annotation instanceof HeadTag) {
            List<Annotation> headTagPath = new ArrayList<Annotation>();
            headTagPath.add(annotation);
            return headTagPath;
        }

        // Recurse all meta annotations
        Annotation[] annotations = annotation.annotationType().getAnnotations();
        for (Annotation metaAnnotation : annotations) {
            // Ignore built-in annotations
            if (metaAnnotation.annotationType().getName()
                    .startsWith("java.lang")) {
                continue;
            }

            List<Annotation> headTagPath = findHeadTagPath(metaAnnotation);
            if (headTagPath != null) {
                headTagPath.add(annotation);
                return headTagPath;
            }
        }

        // Return without a result if nothing found
        return null;
    }

    public static void init(VaadinService service) {
        final BootstrapListener listener = new HeaderTagHandler();
        service.addSessionInitListener(new SessionInitListener() {
            @Override
            public void sessionInit(SessionInitEvent event)
                    throws ServiceException {
                event.getSession().addBootstrapListener(listener);
            }
        });
    }
}
