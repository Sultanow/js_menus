package de.jsmenues.backend.authentication;

import de.jsmenues.redis.data.Configuration;
import de.jsmenues.redis.repository.ConfigurationRepository;

public class Password {
    /**
     * save Password in redis
     * 
     * @param rootPassword : rootPassword is saved als "1234" when  app begins
     */
    public static void setRootPassword(String rootPassword) {
        ConfigurationRepository.getRepo().save(new Configuration("password", rootPassword));
    }

     /**
     * check old and newPassword before change
     * 
     * @param oldPssword and newPassword: passing from frontend
     */
    public static boolean changeRootPassword(String oldPassword, String newPassword) {
        boolean isChanged = false;
        String currentPassword = ConfigurationRepository.getRepo().get("password").getValue();

        if (currentPassword.equals(oldPassword)) {
            ConfigurationRepository.getRepo().save(new Configuration("password", newPassword));
            isChanged = true;
        }
        return isChanged;
    }
}
