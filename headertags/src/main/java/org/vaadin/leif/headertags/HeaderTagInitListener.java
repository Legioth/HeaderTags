package org.vaadin.leif.headertags;

import com.vaadin.server.ServiceInitEvent;
import com.vaadin.server.VaadinServiceInitListener;

public class HeaderTagInitListener implements VaadinServiceInitListener {
    @Override
    public void serviceInit(ServiceInitEvent event) {
        HeaderTagHandler.init(event.getSource());
    }
}
