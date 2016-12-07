package org.vaadin.leif.headertags;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines an arbitrary &lt;meta&gt; tag for the host page of a UI class.
 * <p>
 * To add multiple tags, use {@link MetaTags}
 */
@HeadTag("meta")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
public @interface Meta {
    /**
     * The name attribute value of the meta tag
     * 
     * @return the name attribute
     */
    public String name() default HeadTag.NULL_VALUE;

    /**
     * The content attribute value of the meta tag
     * 
     * @return the content attribute
     */
    public String content() default HeadTag.NULL_VALUE;

    /**
     * The http-equiv attribute value of the meta tag
     * 
     * @return the http-equiv attribute
     */
    public String httpEquiv() default HeadTag.NULL_VALUE;

    /**
     * The charset attribute value of the meta tag
     * 
     * @return the charset attribute
     */
    public String charset() default HeadTag.NULL_VALUE;

    /**
     * The itemprop attribute value of the meta tag
     * 
     * @return the itemprop attribute
     */
    public String itemprop() default HeadTag.NULL_VALUE;
}
