package de.jsmenues.backend.zabbixapi;

import de.jsmenues.redis.data.Configuration;
import de.jsmenues.redis.repository.ConfigurationRepository;
import de.jsmenues.redis.repository.IConfigurationRepository;

public class ZabbixUser {
    private static final String KEY_USER = "configuration.zabbix.User";
    private static final String KEY_PASSWORD = "configuration.zabbix.Password";
    private  static final String KEY_URL = "configuration.zabbix.URL";

    private String username;
    private String password;
    private String zabbixUrl;

    @Deprecated
    public ZabbixUser() {

    }

    public ZabbixUser(String user, String pass, String url) {
        this.username = user;
        this.password = pass;
        this.zabbixUrl = url;
    }

    public static ZabbixUser loadFromRepository(IConfigurationRepository configurationRepository) {
        String username = configurationRepository.getVal(KEY_USER);
        String password = configurationRepository.getVal(KEY_PASSWORD);
        String zabbixUrl = configurationRepository.getVal(KEY_URL);

        // use standard values if none exist in redis
        if (username == "" && password == "" && zabbixUrl == ""){
            username = "Admin";
            password = "zabbix";
            zabbixUrl = "http://zabbix-frontend:80/api_jsonrpc.php";
        }

        return new ZabbixUser(username, password, zabbixUrl);
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

    public void saveUser(IConfigurationRepository configurationRepository) {
        Configuration userConfiguration = new Configuration(KEY_USER, this.username);
        configurationRepository.save(userConfiguration);
        Configuration passConfiguration = new Configuration(KEY_PASSWORD, this.password);
        configurationRepository.save(passConfiguration);
        Configuration urlConfig = new Configuration(KEY_URL, this.zabbixUrl);
        configurationRepository.save(urlConfig);
    }
}
