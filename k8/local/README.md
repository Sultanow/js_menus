# Hackathon - Deployment mit lokalem Kubernetes (Docker-For-Desktop)

## Voraussetzungen:  
Erfolgreiche Installation von:  
1. Docker for Desktop
2. kubectl mit richtiger PATH-Variable

## Bauen der Images
**Hinweis:**  
Unter Windows bitte mit CMD oder Powershell bauen.

### 1. Starte Kubernetes über Docker-for-Desktop
### 2. Verifiziere mit der kubectl ob das Cluster läuft  
``kubectl get cluster info``
### 3. Baue die Dockerimages lokal
Das Bauen ist notwendig, da lokal keine Containerregistry zum Einsatz kommt
#### 3.1. Bauen von Helidon  
``docker build -f docker/helidon/Dockerfile -t hackathon-helidon-mp:v1 .``

Nach diesem Buildbefehl ist das Image lokal unter dem Namen **hackathon-helidon-mp:v1** verfügbar. Der Name ist entscheidend für das erfolgreiche Bereitstellen in k8.
#### 3.2. Bauen der Angular Awendung
``docker build -f docker/angular/Dockerfile -t hackathon-angular:v1 .``

Nach diesem Buildbefehl ist das Image lokal unter dem Namen **hackathon-angular:v1** verfügbar. Der Name ist entscheidend für das erfolgreiche Bereitstellen in k8.

#### 3.3. Redis
Für Redis ist kein Build erforderlich, es wird stattdessen aus dem Docker-Hub-Registry bezogen.

## Bereitstellen in Kubernetes
Für das lokale Deployment stehen .yml-Datein für K8 zur Verfügung: ./k8/local/

#### 3.1. Bereitstellen der Anwendung
``kubectl apply -f helidon.yml ``  
``kubectl apply -f angular.yml ``  
``kubectl apply -f redis.yml ``


