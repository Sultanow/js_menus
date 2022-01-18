# Docker Builder usage
Das Projekt kann gebaut und gestartet werden ohne eine Buildumgebung mit Java, Maven und node.js. Um dieses Ziel zu erreichen wurde der Build-Prozess auf eine Dockerbasierte build Umgebung geändert.

## Prerequesits
- Windows
- Docker for Desktop installiert

## How-to
1. Starten der Powershell als Administrator 
    - Da unter Windows Powershell-Skripte unberbunden werden, müssen Skripte vorrübergehend aktiviert werden:
    - ```Set-ExecutionPolicy RemoteSigned ``` oder (falls erstes nicht funktioniert) ```Set-ExecutionPolicy Unrestricted``` (Unristricted solle nur vorübergehend gesetzt sein)
2. Im Ordner ``/docker/builder`` die Powershell öffnen und das Skript ``createDockerBuildContainer.ps1`` starten. Der default Modus des Skripts baut die Docker-Container für den Build des Projekts und im Anschluss werden auch die Projektartefakte gebaut. Das Skript bietet die Möglichkeit die Container-Images neu zu bauen mit der Option ``-force 1``. Alle Möglichkeiten des Scripts sind hier aufgeführt: ``createDockerBuildContainer.ps1 [ -force 1|0 ] [ -build all|frontend|backend ]``. 
3. Wenn das Skript durchgelaufen ist, sollten im ``/target/autodeploy/`` Ordner mindestend die Files ``frontend.war`` und ``backend.war`` liegen.

Note: Der Build mittels Docker dauert recht lange. Es sollte mindestens eine halbe Stunde für den Build eingeplant werden.

## Zurücksetzen der Powershell ExecutionPolicy
1. Starten der Powershell als Administrator
2. Den Befehlt ``Set-ExecutionPolicy Restricted`` ausführen.
