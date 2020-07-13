package de.jsmenues.listeners;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import de.jsmenues.backend.authentication.TimerToDeleteOldTokens;

public class MyContextListener implements ServletContextListener {

    private ServletContext context = null;
    /*
     * This method is invoked when the Web Application has been removed
     * 
     * and is no longer able to accept requests
     * 
     */

    public void contextDestroyed(ServletContextEvent event)
    {
        this.context = null;
    }

    /* This method is invoked when the Web Application
     * is ready to service requests
     * (will be calling this contextInitialized every 12 hour)
    */
    public void contextInitialized(ServletContextEvent event)
    {
        TimerToDeleteOldTokens timer = new TimerToDeleteOldTokens();
        timer.start();
        this.context = event.getServletContext();
    }

}
