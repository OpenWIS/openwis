package org.openwis.metadataportal.kernel.user;

import jeeves.utils.Log;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by cosmin on 01/08/19.
 */
public class UserServletContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        UserSessionManager userSessionManager = new UserSessionManagerImpl();

        System.out.print("**** User session manager initialized");
        Log.debug("UserSessionManager","User session manager initialized.");
        servletContextEvent.getServletContext().setAttribute("userSessionManager", userSessionManager);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
