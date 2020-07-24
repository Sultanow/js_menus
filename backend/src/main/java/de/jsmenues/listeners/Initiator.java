package de.jsmenues.listeners;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import de.jsmenues.backend.authentication.Password;
import de.jsmenues.backend.authentication.TimerToDeleteOldTokens;
import de.jsmenues.redis.repository.ConfigurationRepository;

/**
 * 
 * Initiator is notified when the application is deployed on the server
 */
public class Initiator implements ServletContextListener {
    private final static String rootPassword = "1234";
    private ServletContext context = null;

    /**
     * This method is invoked when the Web Application has been removed
     * and is no longer able to accept requests
     * 
     */
    public void contextDestroyed(ServletContextEvent event) {
        this.context = null;
    }

    /**
     * This method is invoked when the Web Application is ready to service requests
     * 
     */
    public void contextInitialized(ServletContextEvent event) {

        // set once the rootPassword as "1234" when app begins and there is not a passwo
        String currentPassword = ConfigurationRepository.getRepo().get("password").getValue();
        if (currentPassword == "") {
            Password.setRootPassword(rootPassword);
        }

        TimerToDeleteOldTokens timer = new TimerToDeleteOldTokens();
        timer.start();
        this.context = event.getServletContext();
    }
}
