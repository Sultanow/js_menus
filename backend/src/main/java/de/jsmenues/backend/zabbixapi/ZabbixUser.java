package de.jsmenues.backend.zabbixapi;

import de.jsmenues.redis.data.Configuration;
import de.jsmenues.redis.repository.ConfigurationRepository;
import de.jsmenues.redis.repository.IConfigurationRepository;

public class ZabbixUser {
    private String username;
    private String password;
    private String zabbixUrl;

    public ZabbixUser() {
        username = ConfigurationRepository.getRepo().getVal("configuration.zabbix.User");
        password = ConfigurationRepository.getRepo().getVal("configuration.zabbix.Password");
        zabbixUrl = ConfigurationRepository.getRepo().getVal("configuration.zabbix.URL");
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
        IConfigurationRepository repo = ConfigurationRepository.getRepo();
        Configuration userConfiguration = new Configuration("configuration.zabbix.User", this.username);
        repo.save(userConfiguration);
        Configuration passConfiguration = new Configuration("configuration.zabbix.Password", this.password);
        repo.save(passConfiguration);
        Configuration urlConfig = new Configuration("configuration.zabbix.URL", this.zabbixUrl);
        repo.save(urlConfig);
    }


}
