package org.vaadin.leif.headertags.demo;

import javax.servlet.annotation.WebServlet;

import org.vaadin.leif.headertags.Link;
import org.vaadin.leif.headertags.Meta;
import org.vaadin.leif.headertags.MetaTags;
import org.vaadin.leif.headertags.Viewport;
import org.vaadin.leif.headertags.ViewportGenerator;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

// Sets a basic viewport meta header
@Viewport("width=device-width, initial-scale=1")
// Adds multiple tags of the same type
@MetaTags({
        // Replaces the Vaadin X-UA-Compatible header
        @Meta(httpEquiv = "X-UA-Compatible", content = "hello"),
        @Meta(name = "test", content = "test") })
// And showing how to create a link tag as well
@Link(rel = "foobar", href = "about:blank")
public class DemoUI extends UI {

    // Generator class declared in the UI class is directly used
    public static class MyViewportGenerator implements ViewportGenerator {
        @Override
        public String getViewport(VaadinRequest request) {
            String userAgent = request.getHeader("User-Agent");
            if (userAgent != null && userAgent.toLowerCase().contains("mobile")) {
                return "width=device-width, initial-scale=1, maximum-scale=1";
            } else {
                return "width=900";
            }
        }
    }

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = DemoUI.class)
    public static class Servlet extends VaadinServlet {

    }

    @Override
    protected void init(VaadinRequest request) {
        setContent(new Label("Demonstrates how to declaratively define tags"
                + " that are added to the head of the generated page"));
    }

}