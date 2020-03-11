package de.jsmenues.backend.zabbixapi;

import de.jsmenues.redis.data.Configuration;
import de.jsmenues.redis.repository.ConfigurationRepository;

public class ZabbixUser {
    private String username;
    private String password;
    private String zabbixUrl;

    public ZabbixUser() {
        username = ConfigurationRepository.getRepo().get("zabbixUser").getValue();
        password = ConfigurationRepository.getRepo().get("zabbixPass").getValue();
        zabbixUrl = ConfigurationRepository.getRepo().get("zabbixURL").getValue();
    }

    public ZabbixUser(String user, String pass, String url) {
        this.username = user;
        this.password = pass;
        this.zabbixUrl = url;
    }


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getZabbixUrl() {
        return zabbixUrl;
    }

    @Override
    public String toString() {
        return "User: " + username + "; Password: " + password + "; URL: " + zabbixUrl;
    }

    public void saveUser() {
        ConfigurationRepository repo = ConfigurationRepository.getRepo();
        Configuration userConfiguration = new Configuration("zabbixUser", this.username);
        repo.save(userConfiguration);
        Configuration passConfiguration = new Configuration("zabbixPass", this.password);
        repo.save(passConfiguration);
        Configuration urlConfig = new Configuration("zabbixURL", this.zabbixUrl);
        repo.save(urlConfig);
    }


}
