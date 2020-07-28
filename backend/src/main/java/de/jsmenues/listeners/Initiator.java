package de.jsmenues.listeners;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jsmenues.backend.authentication.Password;
import de.jsmenues.backend.authentication.TimerToDeleteOldTokens;
import de.jsmenues.backend.elasticsearch.ElasticsearchConnecter;
import de.jsmenues.backend.zabbixservice.ZabbixElasticsearchSynchronization;
import de.jsmenues.redis.repository.ConfigurationRepository;

/**
 * Initiator is notified when the application is deployed on the server
 */
public class Initiator implements ServletContextListener {
    public static boolean firstCall = true;
    private static Logger LOGGER = LoggerFactory.getLogger(Initiator.class);
    private final static String rootPassword = "1234";
    private ServletContext context = null;

    /**
     * This method is invoked when the Web Application has been removed and is no
     * longer able to accept requests
     */
    public void contextDestroyed(ServletContextEvent event) {
        this.context = null;
    }

    /**
     * This method is invoked when the Web Application is ready to service requests
     * 
     */
    public void contextInitialized(ServletContextEvent event) {

        LOGGER.info("Application start");
       

        // make sure that old connection is closed
        if (ElasticsearchConnecter.restHighLevelClient != null) {
            try {
                ElasticsearchConnecter.closeConnection();
                LOGGER.info("old connection is closed");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // set once the rootPassword as "1234" when Web Application begins and there is
        // not a password
        String currentPassword = ConfigurationRepository.getRepo().get("password").getValue();
        if (currentPassword == "") {
            Password.setRootPassword(rootPassword);
        }

        TimerToDeleteOldTokens timer = new TimerToDeleteOldTokens();
        timer.start();

        ZabbixElasticsearchSynchronization zabbixElasticsearchSynchronization = new ZabbixElasticsearchSynchronization();
        zabbixElasticsearchSynchronization.start();

        this.context = event.getServletContext();
    }
}
