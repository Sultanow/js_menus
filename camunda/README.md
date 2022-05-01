
# Release-Management-System: Workflow Engine

Dieser Ordner enthält eine Instanz von Camunda Platform 7, die im Release-Management-System des Informations-Cockpits genutzt wird.

## Requirements

- [Node.js with npm](https://nodejs.org/en/download/) (Version im Projekt: 16.14.2)
- [Java 15 64-bit](https://www.oracle.com/java/technologies/javase/jdk15-archive-downloads.html) (Limitiert durch Camunda)
- [Git](https://git-scm.com/download)
- [Eclipse](https://www.eclipse.org/downloads/packages/) (Oder eine andere Java-IDE)
- Geklontes Repository
    ```git clone https://github.com/Sultanow/js_menus.git``` und ggfs. Wechsel auf Branch ```git checkout release-management-system```, sofern noch nicht integriert

## Installation

    1. Eclipse öffnen und das Projekt aus /camunda über "File -> Open Projects From File System or Archive" hinzufügen<br/>
WICHTIG: Darauf achten, im richtigen Unterordner zu sein und dass das Projekt als Maven-Projekt erkannt wird.
(HIER BILD EINFÜGEN)

    2. Nachdem die Quellen aktualisiert wurden, im Package Explorer einen Rechtsklick auf pom.xml ausführen  
       und "Run As -> Maven build.." klicken
    3. In den Build-Optionen unter "Goals" eingeben: "clean install" (ohne Anführungsstriche) und bestätigen
    4. Warten bis Abhängigkeiten heruntergeladen wurden, fertig!
## Start

Um die Workflow-Engine zu starten:  

- Im Package-Explorer: Rechtsklick auf "/src/main/java/com/example/workflow/Application.java"
- "Run As -> Java Application" wählen.  
HINWEIS: Firewall-Freigaben bestätigen

- Nach erfolgreichem Start sollte das Camunda-eigene Cockpit über [http://localhost:8081](http://localhost:8081) in einem Browser aufgerufen werden können

## Authors

- [@jandestiny (Jan Echebiri)](https://www.github.com/jandestiny)


## Appendix

Bei Fragen oder Problemen bitte @jandestiny kontaktieren.

