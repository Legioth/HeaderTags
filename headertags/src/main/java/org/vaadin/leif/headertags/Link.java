package org.vaadin.leif.headertags;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines an arbitrary &lt;link&gt; tag for the host page of a UI class.
 * <p>
 * To add multiple tags, use {@link LinkTags}
 */
@HeadTag("link")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
@Repeatable(LinkTags.class)
public @interface Link {
    /**
     * The rel attribute value of the meta tag
     *
     * @return the rel attribute
     */
    public String rel();

    /**
     * The href attribute value of the meta tag
     *
     * @return the href attribute
     */
    public String href();

    /**
     * The media attribute value of the meta tag
     *
     * @return the media attribute
     */
    public String media() default HeadTag.NULL_VALUE;

    /**
     * The type attribute value of the meta tag
     *
     * @return the type attribute
     */
    public String type() default HeadTag.NULL_VALUE;

    /**
     * The sizes attribute value of the meta tag
     *
     * @return the sizes attribute
     */
    public String sizes() default HeadTag.NULL_VALUE;

    /**
     * The sizes attribute value of the meta tag
     *
     * @return the sizes attribute
     */
    public String title() default HeadTag.NULL_VALUE;
}
