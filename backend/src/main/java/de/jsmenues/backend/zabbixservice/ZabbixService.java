package de.jsmenues.backend.zabbixservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.cgi.zabbix.api.DefaultZabbixApi;
import io.github.cgi.zabbix.api.Request;
import io.github.cgi.zabbix.api.RequestBuilder;
import io.github.cgi.zabbix.api.ZabbixApi;
import de.jsmenues.backend.zabbixapi.ZabbixUser;
import de.jsmenues.redis.repository.ConfigurationRepository;

public class ZabbixService {

    private static Logger LOGGER = LoggerFactory.getLogger(ZabbixService.class);

    public static ObjectMapper mapper = new ObjectMapper();

    /**
     * Is used to login in Zabbix
     * 
     */
    private ZabbixApi zabbixLogin() {
        ZabbixUser user = new ZabbixUser();
        ZabbixApi zabbixApi = new DefaultZabbixApi(user.getZabbixUrl());
        zabbixApi.init();
        try {
            boolean loginsuccess = zabbixApi.login(user.getUsername(), user.getPassword());
            if (!loginsuccess) {
                zabbixApi = null;
            }
        } catch (Exception e) {
            LOGGER.info(e.getMessage() + "\n Zabbix login was not successfully");
            zabbixApi = null;
        }

        return zabbixApi;
    }

    /**
     * Is used to retry calling Zabbix api
     * 
     * @param api     http call
     * @param request login request
     * @param auth    authentication response
     * @param retry   times of calling if request failed
     * 
     * @return response about the request
     */
    public JsonNode call(ZabbixApi api, Request request, boolean auth, int retry) {
        JsonNode getResponse = null;
        boolean recall = true;
        for (int i = 0; (i < retry) && recall; i++) {
            try {
                getResponse = api.call(request, true);
                recall = false;
                LOGGER.info("Zabbix call was sent successfully");
            } catch (Exception e) {
                LOGGER.info(e.getMessage() + "Zabbix call  sent failed");
            }
        }
        if (recall) {
            LOGGER.info("Zabbix call  sent failed more than " + retry + "times");
        }
        return getResponse;
    }

    /**
     * Get all hosts from zabbix
     * 
     * @return list of hosts
     */
    public List<Map<String, Object>> getAllHosts() {
        ZabbixApi api = zabbixLogin();
        if (api == null) {
            return null;
        }

        List<String> hostOutputParams = new LinkedList<>();
        hostOutputParams.add("hostid");
        hostOutputParams.add("host");
        String filterGroup = ConfigurationRepository.getRepo().get("configuration.zabbix.filterGroup").getValue();
        if (filterGroup.isEmpty()) {

            Request hostRequest = RequestBuilder.newBuilder().method("host.get")
                    .paramEntry("output", hostOutputParams.toArray()).build();
            JsonNode getResponse = call(api, hostRequest, true, 3);
            JsonNode result = getResponse.path("result");
            List<Map<String, Object>> mapResult = mapper.convertValue(result,
                    new TypeReference<List<Map<String, Object>>>() {
                    });
            return mapResult;
        } else {
            Map<String, String[]> filter = new HashMap<>();
            filter.put("name", new String[] { filterGroup });
            List<String> outputParams = new LinkedList<>();
            outputParams.add("name");
            outputParams.add("hosts");
            Request hostgroupRequest = RequestBuilder.newBuilder().method("hostgroup.get")
                    .paramEntry("selectHosts", hostOutputParams.toArray()).paramEntry("filter", filter)
                    .paramEntry("output", outputParams).build();
            JsonNode getResponse = call(api, hostgroupRequest, true, 3);
            JsonNode result = getResponse.path("result");

            if (result.isArray()) {
                for (final JsonNode objNode : result) {
                    JsonNode node = objNode.get("hosts");
                    result = node;
                }
            }
            List<Map<String, Object>> mapResult = mapper.convertValue(result,
                    new TypeReference<List<Map<String, Object>>>() {
                    });
            return mapResult;
        }
    }

    /**
     * Get all host info from zabbix
     * 
     * @return list of hosts info
     */
    public List<Map<String, List<Object>>> getHostInfos() {

        ZabbixApi api = zabbixLogin();
        if (api == null) {
            return null;
        }
        List<Map<String, Object>> AllHosts = getAllHosts();

        List<Map<String, Object>> mapAllHosts = mapper.convertValue(AllHosts,
                new TypeReference<List<Map<String, Object>>>() {
                });
        ArrayList<String> hosts = new ArrayList<String>();

        for (Map<String, Object> host : mapAllHosts) {
            Object hostName = host.get("host");
            String stringHostName = String.valueOf(hostName);
            hosts.add(stringHostName);
        }
        String[] hostsArry = hosts.toArray(new String[0]);
        String[] output = { "name", "hostid", "host" };
        String[] selectItems = { "itemid", "key_", "name", "prevvalue", "lastvalue", "lastclock", "description" };
        Map<String, String[]> filter = new HashMap<>();
        filter.put("name", hostsArry);
        Request informationRequest = RequestBuilder.newBuilder().method("host.get").paramEntry("filter", filter)
                .paramEntry("output", output).paramEntry("selectItems", selectItems).paramEntry("selectHosts", "extend")
                .paramEntry("selectGroups", "extend").build();
        JsonNode getResponse = call(api, informationRequest, true, 3);
        JsonNode result = getResponse.path("result");
        List<Map<String, List<Object>>> mapAllHostInfo = mapper.convertValue(result,
                new TypeReference<List<Map<String, Object>>>() {
                });
        return mapAllHostInfo;
    }

    /**
     * Get all history records from zabbix
     * 
     * @return list of history records
     */
    public List<Map<String, Object>> getHistory() {

        ZabbixApi api = zabbixLogin();
        if (api == null) {
            return null;
        }
        List<String> historyOutputParams = new LinkedList<>();
        historyOutputParams.add("clock");
        historyOutputParams.add("itemid");
        historyOutputParams.add("value");

        List<Map<String, List<Object>>> allHostsinfo = getHostInfos();
        List<Map<String, Object>> mapAllHostsInfo = mapper.convertValue(allHostsinfo,
                new TypeReference<List<Map<String, Object>>>() {
                });
        List<String> historyItemids = new LinkedList<>();

        for (Map<String, Object> host : mapAllHostsInfo) {
            Object items = host.get("items");
            List<Map<String, Object>> mapItems = mapper.convertValue(items,
                    new TypeReference<List<Map<String, Object>>>() {
                    });

            for (Map<String, Object> mapItem : mapItems) {
                Object mapitemid = mapItem.get("itemid");
                String itemid = String.valueOf(mapitemid);
                historyItemids.add(itemid);
            }
        }
        Request historyRequest = RequestBuilder.newBuilder().method("history.get").paramEntry("itemids", historyItemids)
                .paramEntry("history", 4).paramEntry("sortfield", "clock").paramEntry("sortorder", "DESC")
                .paramEntry("limit", 10000).paramEntry("output", historyOutputParams).build();
        JsonNode getResponse = call(api, historyRequest, true, 4);
        JsonNode result = getResponse.path("result");
        List<Map<String, Object>> mapHistory = mapper.convertValue(result,
                new TypeReference<List<Map<String, Object>>>() {
                });
        return mapHistory;
    }
}
