package org.vaadin.leif.headertags;

import com.vaadin.server.VaadinRequest;

/**
 * Generates the value of an head tag attribute. If a method in a
 * {@link HeadTag} annotation returns a Class implementing this interface, the
 * return value of {@link #getValue(String, String, VaadinRequest)} is used as
 * the attribute value. An instance of the generator class is created using its
 * default constructor.
 */
public interface AttributeGenerator {
    /**
     * Generates the attribute value. Returning <code>null</code> removes the
     * attribute and an empty string is included as an empty string in the HTML.
     * 
     * @param tag
     *            the name of the tag to generate an attribute value for
     * @param attributeName
     *            the name of the attribute to generate a value for
     * @param request
     *            the HTML page request being processed
     * @return an attribute value, or <code>null</code> to remove the attribute
     */
    public String getValue(String tag, String attributeName,
            VaadinRequest request);
}
