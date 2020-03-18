package de.jsmenues.backend.zabbixapi;

import de.jsmenues.redis.data.Configuration;
import de.jsmenues.redis.repository.ConfigurationRepository;

public class ZabbixUser {
    private String username;
    private String password;
    private String zabbixUrl;

    public ZabbixUser() {
        username = ConfigurationRepository.getRepo().get("configuration.zabbix.User").getValue();
        password = ConfigurationRepository.getRepo().get("configuration.zabbix.Password").getValue();
        zabbixUrl = ConfigurationRepository.getRepo().get("configuration.zabbix.URL").getValue();
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
        return "configuration.zabbix.User: " + username + "; configuration.zabbix.Password: " + password + "; configuration.zabbix.URL: " + zabbixUrl;
    }

    public void saveUser() {
        ConfigurationRepository repo = ConfigurationRepository.getRepo();
        Configuration userConfiguration = new Configuration("configuration.zabbix.User", this.username);
        repo.save(userConfiguration);
        Configuration passConfiguration = new Configuration("configuration.zabbix.Password", this.password);
        repo.save(passConfiguration);
        Configuration urlConfig = new Configuration("configuration.zabbix.URL", this.zabbixUrl);
        repo.save(urlConfig);
    }


}
