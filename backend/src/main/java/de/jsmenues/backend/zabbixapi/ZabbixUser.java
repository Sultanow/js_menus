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


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getZabbixUrl() {
        return zabbixUrl;
    }
}
