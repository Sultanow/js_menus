

# Release-Management-System

Dieser Ordner enthält die Angular-Komponente des Release-Management-Systems.

## Requirements

- Installation und Start der Camunda Workflow-Engine, siehe [Camunda-Readme]()
- Installation und Start des Angular-Frontends [Angular-Readme]()

## Ausführung
- Sicher stellen, dass Workflow-Engine unter [http://localhost:8081](http://localhost:8081) erreichbar ist.
- Angular-Frontend unter [http://localhost:4200](http://localhost:4200) aufrufen.

## Wechsel des verwendeten BPMN-Prozesses bei Namensänderung
- Im Angular-Projekt unter /src/environments/environment.ts, die Variable "camundaReleaseManagementProcessKey" anpassen.

## Architekturbild


