from pyzabbix import ZabbixMetric, ZabbixSender
from pyzabbix.api import ZabbixAPI
import json

def generate_item_params(name, key_, applicationid):
    param = {
        "name": name,
        "key_": key_,
        "applications" : [applicationid],
        "hostid": templateid,
        "type": 2, #Zabbix Trapper
        "value_type": 4 #Text
    }

    return param

def generate_host(host):
    param = {
        "host": host,
        "groups": [{
               'groupid': groupid
        }],
        "templates": [{
                "templateid": templateid
        }],
        "interfaces": [{
                "type": 1,
                "main": 1,
                "useip": 1,
                "ip": "127.0.0.1",
                "dns": "",
                "port": "10050"
        }],

    }

    return param

zapi = ZabbixAPI(url='http://localhost:8081/api_jsonrpc.php', user='Admin', password='zabbix',)

# Create the hostgroup
answer = zapi.do_request('hostgroup.create', params={"name": "Hostgroup1"})
groupid = answer['result']['groupids'][0]
print("GroupID: ", groupid)

# Create the Template added to the Hostgroup
answer = zapi.do_request('template.create', params={"host": "TestTemplate", "groups" : {"groupid": groupid}})
templateid = answer['result']['templateids'][0]
print("TemplateID: ", templateid)

# Create Application
answer = zapi.do_request('application.create', params={"name": "TestApplication", "hostid": templateid})
applicationid = answer['result']['applicationids'][0]
print ("ApplicationID: ", applicationid)

# Create the Items 
CREATEITEM='item.create'
answer = zapi.do_request(CREATEITEM, params=generate_item_params("Hilfsapplication-Version", "version.helperapplication", applicationid))
answer = zapi.do_request(CREATEITEM, params=generate_item_params("Silbentrennung", "config.silbentrennung", applicationid))
answer = zapi.do_request(CREATEITEM, params=generate_item_params("SW-Version", "version.software", applicationid))
answer = zapi.do_request(CREATEITEM, params=generate_item_params("Text-Version", "version.text", applicationid))

# Last but not least create the hosts
CREATEHOST='host.create'
zapi.do_request(CREATEHOST, params=generate_host("dev1"))
zapi.do_request(CREATEHOST, params=generate_host("dev2"))
zapi.do_request(CREATEHOST, params=generate_host("dev3"))
zapi.do_request(CREATEHOST, params=generate_host("dev4"))
zapi.do_request(CREATEHOST, params=generate_host("dev5"))

print ("End of default value script")
