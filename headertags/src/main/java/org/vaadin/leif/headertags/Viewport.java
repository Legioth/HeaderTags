package org.vaadin.leif.headertags;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Defines a viewport meta tag for the host page of a UI class.
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Meta(name = "viewport")
public @interface Viewport {
    /**
     * The viewport definition.
     * 
     * @return the viewport definition
     */
    @HeadTagAttribute("content")
    String value();
}
