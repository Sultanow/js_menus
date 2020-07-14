package de.jsmenues.backend.authentication;

import de.jsmenues.redis.data.Configuration;
import de.jsmenues.redis.repository.ConfigurationRepository;

public class Password {
    /**
     * save Password in redis
     * 
     * @param rootPassword : rootPassword is saved als "1234" when  app begins
     * @return 
     */
    public static boolean setRootPassword(String rootPassword) {
        boolean isSaved=false;
        ConfigurationRepository.getRepo().save(new Configuration("password", rootPassword));
        String currentPassword = ConfigurationRepository.getRepo().get("password").getValue();
        if(rootPassword.equals(currentPassword))
            isSaved= true;
        return isSaved;
    }

     /**
     * check old and new Password before change
     * 
     * @param oldPssword and new Password passing from frontend
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
