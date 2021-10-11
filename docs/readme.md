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
    - In das Verzeichnis /frontend mit der git bash wechseln
    - Mit `mvn clean install` das Frontend bauen
  2. Backend:
    - In das Verzeichnis /backend mit der git bash wechseln
    - Mit `mvn clean install` das Backend bauen
  3. Dateien backend.war und frontend.war von dem Verzeichnis js_menus\target\autodeploy in das Verzeichnis js_menus\target verschieben. Der Ordner autodeploy sollte nun leer sein.

3. Starten von Payara und Redis als Container
  - Mit der Powershell in das Verzeichnis /docker wechseln
  - Mit `docker-compose up -d` den Payara und Redis Service starten
  - Auf der Payara Konsole unter https://localhost:4848/ mit Benutzername "admin" und Password "admin" einloggen
  - Auf "List Deployed Applications" klicken
  - Die Liste sollte leer sein, ansonsten alle Einträge auswählen und auf "Undeploy" klicken
  - Auf "Deploy..." klicken und darauhin die backend.war Datei auswählen die im Verzeichnis js_menus\target liegt
  - Auf "OK" klicken und analog für frontend.war ausführen

Der Folgende Schritt ist derzeit nicht möglich:
4. Setzen der Einstellungen für Zabbix in der Weboberfläche
  - Öffnen des Dashboards im Browser mit http://localhost:8080
  - Klick auf das Zahnrad oben rechts
  - Setzen der Zabbix Konfiguration:
    - User: Admin
    - Passwort: zabbix
    - URL: http://zabbix-frontend:80/api_jsonrpc.php

5. Die Konfigurationsparameter werden [hier](parameterbeschreibung.md) extra beschrieben mit Beispielen
6. Angular Components
  Manchmal sind nicht alle Angular Module richtig importiert. In diesem Fall wechseln Sie zum /angular Verzeihniss (.\js_menus\angular) und führen Sie folgende befehle aus. Danach können Sie mit ng serve kompilieren
  - npm install --save-dev @angular-devkit/build-angular
  - npm install --save sweetalert2 (wird nicht als Teil der devkit importiert)
  - ng serve

## Automatisiertes Bauen ohne lokal installierte Build-Umgebung

1. Der Anleitung für [Build mit Docker](DockerBuilderUse.md) befolgen.
2. Für den ersten Startup sollten das Startup Skript verwendet werden, da dies auch zusätzliche initialisierungen übernimmt.

## Aufbau einer Elasticsearch-Datenbank 
Bevor man Elasticsearch aufbaut, sollte man Docker neu eingestellt sein.
Dazu geht man auf Docker-Settings und stellt die Recourcen wie folgt ein:

   - Cpu auf 4 kern
   - Memory zwischen 3 oder 4 Giga
   - Swap zwichen 2 and 4 Giga

1.  Diese Elasticsearch-Konfiguration ist nur für Testzwecke. Es gibt dafür 3 Varianten:

  a. Single-Node-Konfiguration stellt einen Knoten zu Verfügung. 
  b. Multipele-Nodes-Konfiguration stellt einen Master- und einen Data-Knoten zu Verfügung. 
  c. Multipele-Nodes-Konfiguration stellt einen Master-, zwei Data- and einen Koordinating-Knoten zu Verfügung.

Hinweis:
  - Hard-disk muss zu mindestens 10% frei sein
  - Für die dritte Variante sollte RAM mindestens 16GB betragen und dockr-memory mindesten 5GB

2. Starten Elasticsearch-Service einer Variante: Dafür eine Powershell öffnen und in das Verzeichnis /docker/elasticsearch/"eine Variante" wechseln.
  - Mit `docker-compose up -d` den Elasticsearch-Service starten.
  - Damit man Daten in Kibanan finden kann, werden Daten in zabbix gebraucht
  - Ausführen des Scripts: `python .\createDefaultConfig.py` (wenn man sich über Frontend anmelden kann)
  - Ausführen des Scripts: `python .\updateItemValue.py`

  3. Öffnen des Kibana-Dashboards im Browser mit http://localhost:5601
  - Ausführen des folgenden GET-Requests in Kibana-Console
  z.B:
    - GET _cat/indices?v
    - GET _cat/node?v
    - GET host_information/_search
    - GET history-dev1-*/_search
    - GET history-dev1-config.silbentrennung/_search


# Fehlerbehebungen

Falls beim Starten des Zabbix Containers folgende Meldung wiederholt erscheint:

**** MySQL server is not available. Waiting 5 seconds...

Dann die Container herunterfahren, den Inhalt dieses Ordners löschen: "js_menus\docker\zabbix\zbx_env\var\lib\mysql" und die Container wieder hochfahren.