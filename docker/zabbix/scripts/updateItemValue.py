from pyzabbix import ZabbixMetric, ZabbixSender

metrics = []
m1 = ZabbixMetric('dev1', 'version.text', 'test1')
m2 = ZabbixMetric('dev2', 'version.text', 'test1')
m3 = ZabbixMetric('dev3', 'version.text', 'test1')
m4 = ZabbixMetric('dev4', 'version.text', 'test1')
m5 = ZabbixMetric('dev5', 'version.text', 'test1')

m6 = ZabbixMetric('dev1', 'version.helperapplication', 'test1')
m7 = ZabbixMetric('dev2', 'version.helperapplication', 'test1')
m8 = ZabbixMetric('dev3', 'version.helperapplication', 'test1')
m9 = ZabbixMetric('dev4', 'version.helperapplication', 'test1')
m10 = ZabbixMetric('dev5', 'version.helperapplication', 'test1')

m11 = ZabbixMetric('dev1', 'version.software', 'test1')
m12 = ZabbixMetric('dev2', 'version.software', 'test1')
m13 = ZabbixMetric('dev3', 'version.software', 'test1')
m14 = ZabbixMetric('dev4', 'version.software', 'test1')
m15 = ZabbixMetric('dev5', 'version.software', 'test1')

m16 = ZabbixMetric('dev1', 'config.silbentrennung','test1')
m17 = ZabbixMetric('dev2', 'config.silbentrennung', 'test1')
m18 = ZabbixMetric('dev3', 'config.silbentrennung', 'test1')
m19 = ZabbixMetric('dev4', 'config.silbentrennung', 'test1')
m20 = ZabbixMetric('dev5', 'config.silbentrennung', 'test1')

m21 = ZabbixMetric('dev1', 'cpu.usage', '20')
m22 = ZabbixMetric('dev2', 'cpu.usage', '45')
m23 = ZabbixMetric('dev3', 'cpu.usage', '80')
m24 = ZabbixMetric('dev4', 'cpu.usage', '90')
m25 = ZabbixMetric('dev5', 'cpu.usage', '35')

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

metrics.append(m21)
metrics.append(m22)
metrics.append(m23)
metrics.append(m24)
metrics.append(m25)

zbx = ZabbixSender('127.0.0.1',10051)
zbx.send(metrics)
