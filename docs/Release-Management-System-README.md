

# Release-Management-System

Dieser Ordner enthält die Angular-Komponente des Release-Management-Systems.

![rms-nutzung-gif](https://user-images.githubusercontent.com/39858598/166148392-3ff42545-76a7-4ace-b20c-74885f009d4b.gif)

## Requirements

- Installation und Start der Camunda Workflow-Engine, siehe [Camunda-Readme](/camunda/README.md)
- Installation und Start des Angular-Frontends [Angular-Readme](/angular/README.md)

## Ausführung
- Sicher stellen, dass Workflow-Engine unter [http://localhost:8081](http://localhost:8081) erreichbar ist.
- Angular-Frontend unter [http://localhost:4200](http://localhost:4200) aufrufen.

## Wechsel des verwendeten BPMN-Prozesses bei Namensänderung
- Im Angular-Projekt unter /src/environments/environment.ts, die Variable "camundaReleaseManagementProcessKey" anpassen.

## Architektur

![architektur](https://user-images.githubusercontent.com/39858598/166148190-2596543d-122d-4f7d-bc6e-fd224c3340d7.jpg)


