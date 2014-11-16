package org.vaadin.leif.headertags;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines the head tag generator classes to use for a UI class. In addition to
 * generators defined using things annotation, generator classes that are
 * declared inside a UI class are also automatically used for that UI class.
 * <p>
 * A generator class is a class that is directly or indirectly generated with
 * {@link HeadTag}. The type is used in the same way as a tag annotation, but
 * instead of using annotation properties, the attribute values are found by
 * creating an instance of the generator type using the default constructor and
 * invoking its methods.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
public @interface HeadTagGenerators {
    /**
     * The generator classes to use for the annotated UI class
     * 
     * @return an array of generator classes to use
     */
    public Class<?>[] value();
}
