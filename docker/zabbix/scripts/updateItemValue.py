from pyzabbix import ZabbixMetric, ZabbixSender

metrics = []
m1 = ZabbixMetric('dev1', 'version.text', 'test-text')
m2 = ZabbixMetric('dev2', 'version.text', 'test-text')
m3 = ZabbixMetric('dev3', 'version.text', 'test-text')
m4 = ZabbixMetric('dev4', 'version.text', 'test-text')
m5 = ZabbixMetric('dev5', 'version.text', 'test-text')

m6 = ZabbixMetric('dev1', 'version.helperapplication', 'test-helperapplication')
m7 = ZabbixMetric('dev2', 'version.helperapplication', 'test-helperapplication')
m8 = ZabbixMetric('dev3', 'version.helperapplication', 'test-helperapplication')
m9 = ZabbixMetric('dev4', 'version.helperapplication', 'test-helperapplication')
m10 = ZabbixMetric('dev5', 'version.helperapplication','test-helperapplication')

m11 = ZabbixMetric('dev1', 'version.software', 'test-software')
m12 = ZabbixMetric('dev2', 'version.software', 'test-software')
m13 = ZabbixMetric('dev3', 'version.software', 'test-software')
m14 = ZabbixMetric('dev4', 'version.software', 'test-software')
m15 = ZabbixMetric('dev5', 'version.software', 'test-software')

m16 = ZabbixMetric('dev1', 'config.silbentrennung','test-silbentrennung')
m17 = ZabbixMetric('dev2', 'config.silbentrennung', 'test-silbentrennung')
m18 = ZabbixMetric('dev3', 'config.silbentrennung', 'test-silbentrennung')
m19 = ZabbixMetric('dev4', 'config.silbentrennung', 'test-silbentrennung')
m20 = ZabbixMetric('dev5', 'config.silbentrennung', 'test-silbentrennung')



metrics.append(m1)
metrics.append(m2)
metrics.append(m3)
metrics.append(m4)
metrics.append(m5)

metrics.append(m6)
metrics.append(m7)
metrics.append(m8)
metrics.append(m9)
metrics.append(m10)

metrics.append(m11)
metrics.append(m12)
metrics.append(m13)
metrics.append(m14)
metrics.append(m15)

metrics.append(m16)
metrics.append(m17)
metrics.append(m18)
metrics.append(m19)
metrics.append(m20)


zbx = ZabbixSender('127.0.0.1',10051)
zbx.send(metrics)
