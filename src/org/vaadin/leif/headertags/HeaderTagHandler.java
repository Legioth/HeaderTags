package org.vaadin.leif.headertags;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import com.vaadin.server.BootstrapFragmentResponse;
import com.vaadin.server.BootstrapListener;
import com.vaadin.server.BootstrapPageResponse;
import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.VaadinRequest;
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

        appendHeadTagAnnotations(fakeHead, uiClass, response.getRequest());

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
            Class<? extends UI> uiClass, VaadinRequest request) {
        processAnnotatedType(head, request, uiClass, null);

        Set<Class<?>> generatorCandidates = new HashSet<Class<?>>(
                Arrays.asList(uiClass.getDeclaredClasses()));
        HeadTagGenerators generatorAnnotation = uiClass
                .getAnnotation(HeadTagGenerators.class);
        if (generatorAnnotation != null) {
            generatorCandidates.addAll(Arrays.asList(generatorAnnotation
                    .value()));
        }

        for (Class<?> generatorClass : generatorCandidates) {
            processGeneratorClass(head, request, generatorClass);
        }
    }

    private void processGeneratorClass(Element head, VaadinRequest request,
            Class<?> generatorClass) {
        // @Inherited does not inherit from interfaces, must check manually
        for (Class<?> iface : generatorClass.getInterfaces()) {
            processAnnotatedType(head, request, iface, generatorClass);
        }

        processAnnotatedType(head, request, generatorClass, generatorClass);
    }

    private void processAnnotatedType(Element head, VaadinRequest request,
            Class<?> declaringClass, Class<?> instanceClass) {
        for (Annotation annotation : declaringClass.getAnnotations()) {
            checkHeadTagAnnotation(head, annotation, request, declaringClass,
                    instanceClass);
        }
    }

    private void checkHeadTagAnnotation(Element head, Annotation annotation,
            VaadinRequest request, Class<?> declaringClass,
            Class<?> instanceClass) {
        // Check if a path to a HeadTag meta annotation can be found
        List<Annotation> headTagPath = findHeadTagPath(annotation);
        if (headTagPath != null) {
            appendHeadTag(head, headTagPath, request, declaringClass,
                    instanceClass);
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
                        checkHeadTagAnnotation(head, member, request,
                                declaringClass, instanceClass);
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

    private void appendHeadTag(Element head, List<Annotation> headTagPath,
            VaadinRequest request, Class<?> declaringClass,
            Class<?> instanceClass) {
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
                appendAttribute(request, element, attribAnnotation, method);
            }
        }

        if (instanceClass != null) {
            try {
                Object instance = instanceClass.newInstance();
                Method[] methods = declaringClass.getMethods();
                for (Method method : methods) {
                    if (method.getDeclaringClass() == Object.class) {
                        continue;
                    }
                    appendAttribute(request, element, instance, method);
                }

            } catch (Exception e) {
                throw new RuntimeException("Error processing annotated type "
                        + declaringClass.getCanonicalName(), e);
            }
        }
    }

    private void appendAttribute(VaadinRequest request, Element element,
            Object targetInstance, Method method) {
        try {
            Class<?>[] parameterTypes = method.getParameterTypes();
            Object[] parameters = new Object[parameterTypes.length];

            for (int i = 0; i < parameterTypes.length; i++) {
                Class<?> type = parameterTypes[i];
                if (type == VaadinRequest.class) {
                    parameters[i] = request;
                } else {
                    throw new RuntimeException(
                            "Unsupported generator parameter type: "
                                    + type.getCanonicalName());
                }
            }

            String value = (String) method.invoke(targetInstance, parameters);

            String name = getHeadTagAttributeName(method);

            if (value == null || HeadTag.NULL_VALUE.equals(value)) {
                element.removeAttr(name);
            } else {
                element.attr(name, value);
            }
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error processing @HeadTag annotation method "
                            + method.getDeclaringClass().getName() + "."
                            + method.getName(), e);
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
