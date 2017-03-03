package org.vaadin.leif.headertags;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vaadin.server.VaadinRequest;

/**
 * Defines the tag name of an HTML tag that can be added to the head of the host
 * page for a UI class.
 * <p>
 * For annotations marked with {@code HeadTag} (for example {@link Meta}), the
 * methods of that annotation are used to define attributes of a corresponding
 * HTML tag that is added to the bootstrap page for a UI. Returning the special
 * value {@link #NULL_VALUE} removes the attribute and an empty string is
 * included as an empty string in the HTML.
 * <p>
 * By default, the method name is used as the attribute name, with
 * <code>camelCase</code> rewritten as <code>camel-case</code>. The name mapping
 * can also be redefined using {@link HeadTagAttribute}.
 * <p>
 * Tag annotations can be refined further by using that annotation as a meta
 * annotation on another annotation. See {@link Viewport} for an example of this
 * usage.
 * <p>
 * A tag annotation can also be used on a non-annotation type to make it work as
 * an attribute generator. Generators can be defined for a UI class using
 * {@link HeadTagGenerators}. Generator classes that are defined as inner
 * classes of a UI class are also used as generators. Methods in generator types
 * can optionally accept a parameter of type {@link VaadinRequest}.
 * <p>
 * Support for multiple annotations of the same type can also be used by
 * defining a collection annotation where value() returns an array of compatible
 * annotations. See {@link MetaTags} for an example of this usage. It's also
 * recommended to add {@link Repeatable} to the main annotation to enable
 * directly adding multiple similar annotation to a UI instead of using the
 * collection annotation.
 *
 * @see Meta
 * @see Viewport
 */

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.TYPE)
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
