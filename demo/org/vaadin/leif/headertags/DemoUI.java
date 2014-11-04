package org.vaadin.leif.headertags;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

// Sets a meta viewport header
@Viewport("width=device-width, initial-scale=1")
// How to add multiple tags of the same type
@MetaTags({
        // Replaces the Vaadin X-UA-Compatible header
        @Meta(httpEquiv = "X-UA-Compatible", content = "hello"),
        @Meta(name = "test", content = "test") })
// And showing how to create a link tag as well
@Link(rel = "foobar", href = "about:blank")
public class DemoUI extends UI {

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = DemoUI.class)
    public static class Servlet extends VaadinServlet {
        @Override
        public void init(ServletConfig servletConfig) throws ServletException {
            super.init(servletConfig);

            // Hook up with the framework's host page generation
            HeaderTagHandler.init(getService());
        }
    }

    @Override
    protected void init(VaadinRequest request) {
        setContent(new Label("Demonstrates how to declaratively define tags"
                + " that are added to the head of the generated page"));
    }

}