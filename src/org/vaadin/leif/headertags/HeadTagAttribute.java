package org.vaadin.leif.headertags;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used on methods in annotations marked with {@link HeadTag} to override the
 * default attribute name mapping.
 * 
 * @see HeadTag
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface HeadTagAttribute {
    /**
     * The attribute name to use for the annotation method annotated with this
     * annotation.
     * 
     * @return the attribute name
     */
    public String value();
}
