package org.vaadin.leif.headertags;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines multiple &lt;link&gt; tags for the host page of a UI class.
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.TYPE)
@Inherited
public @interface LinkTags {
    /**
     * The link tags to include on the host page
     * 
     * @return the link tags
     */
    public Link[] value();

}
