# Serverless Architektur mit Payara, Redis, Angular - Informations Cockpit

Dieses Projekt ist zur zentralen Bereitstellung von Informationen über ein Informations Cockpit.

Die Anwendung besteht aus einem, mithilfe von Docker, containerisierten Payara Server, einer Redis-Instanz und einer Python-Instanz. Das Backend und die Angular-Anwendung laufen im Payara Server als Anwendungen und stellen ihre Dienste als Webservice bereit. Die Angular-Anwendung besteht aus einem SVG-Menu (Siehe Dmeo unter Anwendung). Dieses dient als zentrale Anlaufstelle für den Zugriff auf verschiedene, im Unternehmen verwendete, Plattformen sowie den schnellen Zugriff auf Reportdaten wie beispielsweise die Laufzeit von nächtlich laufenden Batches in einer Produktiv- oder Testumgebung. Diese und weitere Features sind momentan in der Entwicklung.

# Anwendung
![Demo](docs/img/demo.gif)

# Quickstart Anleitung
[Quickstart](docs/readme.md)

# Grundlegende Referenzarchitektur
TODO neues Bild generieren

# Technologienstack

## Application Server
+ [Payara](https://www.payara.fish/)

## Plattform
+ [Docker](https://www.docker.com/)
+ [NGINX](https://nginx.org/en/)

## Speicher
+ [Redis](https://redis.io/)

## Verarbeitende Services
+ [Python](https://www.python.org/)

