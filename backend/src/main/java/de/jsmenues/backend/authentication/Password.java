package de.jsmenues.backend.authentication;

import de.jsmenues.redis.data.Configuration;
import de.jsmenues.redis.repository.ConfigurationRepository;

public class Password {
    /**
     * save rootPassword in redis
     * 
     * @param rootPassword  rootPassword is saved als "1234" when the application
     *                     is deployed on the server if there is no password
     * 
     * @return password is saved in redis true or false
     */
    public static boolean setRootPassword(String rootPassword) {

        ConfigurationRepository.getRepo().save(new Configuration("password", rootPassword));
        String currentPassword = ConfigurationRepository.getRepo().getVal("password");
        return rootPassword.equals(currentPassword);
    }

    /**
     * check old and new Password before change
     * 
     * @param oldPssword  passing from frontend
     * @param newPassword passing from frontend
     * @return password is changend true or false
     */
    public static boolean changeRootPassword(String oldPassword, String newPassword) {
        String currentPassword = ConfigurationRepository.getRepo().getVal("password");

        if (currentPassword.equals(oldPassword)) {
            ConfigurationRepository.getRepo().save(new Configuration("password", newPassword));
        }
        return currentPassword.equals(oldPassword);
    }
}
