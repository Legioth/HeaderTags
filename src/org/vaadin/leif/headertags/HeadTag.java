package org.vaadin.leif.headertags;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Meta annotation for defining the tag name of an annotation that can be used
 * on a UI class to define HTML tags that are added to the head of the host
 * page.
 * <p>
 * For annotation types marked with {@code HeadTag} (for example {@link Meta}),
 * the methods of that annotation are used to define attributes of a
 * corresponding HTML tag that is added to the bootstrap page for a UI.
 * <p>
 * By default, the name of the annotation method is used as the attribute name,
 * with <code>camelCase</code> rewritten as <code>camel-case</code>. The name
 * mapping can also be redefined using {@link HeadTagAttribute}. Attributes
 * carrying the special value {@link #NULL_VALUE} are ignored whereas empty
 * strings are included as empty strings in the HTML.
 * <p>
 * Tag annotations can be refined further by using that annotation as a meta
 * annotation on another annotation. See {@link Viewport} for an example of this
 * usage.
 * <p>
 * Support for multiple annotations of the same type can also be used by
 * defining a collection annotation where value() returns an array of compatible
 * annotations. See {@link MetaTags} for an example of this usage.
 * 
 * @see Meta
 * @see Viewport
 */

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.ANNOTATION_TYPE)
public @interface HeadTag {
    /**
     * The tag name to use for for annotations annotated with this type.
     * 
     * @return the HTML tag name
     */
    public String value();

    /**
     * Special value used to represent attribute values that should be ignored.
     */
    public static final String NULL_VALUE = "_specialValueRepresentingNull_";
}
