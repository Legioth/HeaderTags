package org.vaadin.leif.headertags;

import com.vaadin.server.VaadinRequest;

/**
 * Dynamically generates the content of a viewport meta tag based on a request.
 * <p>
 * To make a UI class use a generator, add a {@link HeadTagGenerators}
 * annotation to the UI class, pointing to the implementation class. If the
 * generator class is defined as an inner class of the UI, it is automatically
 * used without a {@link HeadTagGenerators} annotation.
 */
@Meta(name = "viewport")
public interface ViewportGenerator {

    /**
     * Generates the viewport definition based on a {@link VaadinRequest}.
     * 
     * @param request
     *            the request
     * @return the viewport meta tag content
     */
    @HeadTagAttribute("content")
    public String getViewport(VaadinRequest request);
}
