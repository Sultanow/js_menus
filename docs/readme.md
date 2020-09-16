# Starten der Containerplattform.

## Manuelles Bauen mit lokal installierter Build-Umgebung
1. Es ist kein Zabbix im Netzwerk verfügbar:
  - Disclaimer: Diese Zabbix Konfiguration ist nur für Testzwecke und als Minimalkonfiguration gedacht, um das Dashboad nutzen zu können!
  - Zuerst starten des Zabbix Service: Dafür eine Powershell öffnen und in das Verzeichnis /docker/zabbix wechseln.
  - Mit `docker-compose up -d` den Service starten.
  - Einloggen ins Zabbix erfolgt über localhost:8081 mit dem User "Admin" und Password "zabbix" (Groß-/Kleinschreibung beachten für den User).  
  - Die Defaultkonfiguration für Zabbix kann mittels eines Pythonscripts geladen werden. Dies ist nicht unbedingt notwendig.
    1. Installieren von Python3 (Die Verwendung innerhalb von Docker ist noch nicht eingerichtet.)
    2. Installieren der Python Bibliothek py-zabbix mit `pip install py-zabbix`
    3. Wechsel in den Ordner /docker/zabbix/scripts
    4. Ausführen des Scripts in der Powershell: `python .\createDefaultConfig.py` (wenn man sich über das Frontend anmelden kann)

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

4. Setzen der Einstellungen für Zabbix in der Weboberfläche
  - Öffnen des Dashboards im Browser mit http://localhost:8080
  - Klick auf das Zahnrad oben rechts
  - Setzen der Zabbix Konfiguration:
    - User: Admin
    - Passwort: zabbix
    - URL: http://zabbix-frontend:80/api_jsonrpc.php

5. Die Konfigurationsparameter werden [hier](parameterbeschreibung.md) extra beschrieben mit Beispielen

## Automatisiertes Bauen ohne lokal installierte Build-Umgebung

1. Der Anleitung für [Build mit Docker](DockerBuilderUse.md) befolgen.
2. Für den ersten Startup sollten das Startup Skript verwendet werden, da dies auch zusätzliche initialisierungen übernimmt.

