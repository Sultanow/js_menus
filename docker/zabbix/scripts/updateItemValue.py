from pyzabbix import ZabbixMetric, ZabbixSender

metrics = []
m = ZabbixMetric('dev2', 'version.text', 'test123')
metrics.append(m)
zbx = ZabbixSender('127.0.0.1',10051)
zbx.send(metrics)
