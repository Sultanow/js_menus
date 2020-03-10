Starten der Containerplattform.

1. Es ist kein Zabbix im Netzwerk verfügbar:
  - Disclamer: Diese Zabbix Konfiguration ist nur für Testzwecke und als Minimalkonfiguration gedacht, um das Dashboad nutzen zu können!
  - Zuerst starten des Zabbix Service: Dafür eine Powershell öffnen und in das Verzeichnis /docker/zabbix wechseln.
  - Mit `docker-compose up -d` den Service starten.
  - Einloggen ins Zabbix erfolgt über localhost:8081 mit dem User "Admin" und Password "zabbix" (Groß-/Kleinschreibung beachten für den User).  

2. Bauen der *.war Files: (mvn sollte im Path liegen)
  1. Frontend:
    - In das Verzeichnis /fronend mit der git bash wechseln
    - Mit `mvn clean install` das Frontend bauen
  2. Backend:
    - In das Verzeichnis /backend mit der git bash wechseln
    - Mit `mvn clean install` das Backend bauen

3. Starten von Payara und Redis als Container
  - Mit der Powershell in das Verzeichnis /docker wechseln
  - Mit `docker-compose up -d` den Payara und Redis Service starten

