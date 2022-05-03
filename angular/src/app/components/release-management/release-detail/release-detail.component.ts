import { Component, OnInit, Input, Output, EventEmitter, ElementRef, ViewChild, AfterViewInit } from '@angular/core';
import { Release } from 'src/app/model/release';
import * as BpmnJS from 'bpmn-js/dist/bpmn-viewer.production.min.js';
import { CamundaApiConnectorService } from 'src/app/services/releasemanagement/camunda-api-connector.service';
import { MatDialog } from '@angular/material/dialog';
import { EditTaskComponent } from "src/app/components/release-management/edit-task/edit-task.component";
import { CamundaTask } from 'src/app/model/camunda-task';
import { ReleaseVariable } from 'src/app/model/release-variable';

@Component({
  selector: 'app-release-detail',
  templateUrl: './release-detail.component.html',
  styleUrls: ['./release-detail.component.css']
})
export class ReleaseDetailComponent implements OnInit, AfterViewInit {

  @ViewChild('bpmncontainer', { static: true }) private bpmnContainer: ElementRef;

  @Input() release: Release = null;
  @Output() emitRelease = new EventEmitter<Release>()

  public isLoading: boolean = false;

  constructor(private camunda: CamundaApiConnectorService, public dialog: MatDialog) { }

  ngOnInit(): void {
    this.loadReleaseTasks()
  }

  ngAfterViewInit(): void {
    //Called after ngAfterContentInit when the component's view has been initialized. Applies to components only.
    //Add 'implements AfterViewInit' to the class.
    this.loadBpmnDiagram()
  }

  loadReleaseTasks() {
    this.camunda.getReleaseTasksByInstanceId(this.release.processInstanceId)
      .subscribe(tasks => {
        this.release.tasks = tasks;
      })
  }

  updateReleaseVariables() {
    this.isLoading = true;

    this.camunda.getAllReleaseVariablesByInstanceId(this.release.processInstanceId)
      .subscribe(
        updatedVariables => {
        this.release.variables = updatedVariables;
        this.isLoading = false;
        },
        error => {
          this.isLoading = false;
          console.warn("Keine Variablen gefunden, Instanz womöglich gelöscht", error)
          this.closeDetailView()
        })
  }

  loadBpmnDiagram() {
    this.camunda.getReleaseBpmnDiagram(this.release.processDefinitionId)
      .subscribe(xml => {

        let viewer = new BpmnJS({
          container: this.bpmnContainer.nativeElement
        })

        try {
          viewer.importXML(xml);
        } catch (error) {
          console.error(error)
        }
      })
  }

  openTaskDialog(task: CamundaTask) {
    const dialogReference = this.dialog.open(EditTaskComponent, {
      data: task
    });

    dialogReference.afterClosed().subscribe(() => {
        this.loadReleaseTasks();
        this.updateReleaseVariables();
    })
  }

  closeDetailView() {
    this.emitRelease.emit(null)
  }

  updateVariable(variableName: string, newValue: string) {
    this.isLoading = true;

    this.camunda.updateVariableByName(this.release.processInstanceId, variableName, newValue)
      .subscribe(() => {
        let newReleaseVariable: ReleaseVariable = { type: "String", value: newValue, valueInfo: null };
        this.release.variables.set(variableName, newReleaseVariable);
        this.isLoading = false;
      },
        error => {
        console.error("Variable konnte nicht aktualisiert werden", error)
        this.isLoading = false;
      })
  }

  confirmDeletionDialog(templateRef) {
    let dialogRef = this.dialog.open(templateRef, {
      width: '250px'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result == "delete") {
        this.deleteProcessInstance()
      }
    });
  }

  deleteProcessInstance() {
    this.camunda.deleteProcessInstanceById(this.release.processInstanceId)
      .subscribe(
        success => {
          //TODO: Success notification
          console.info("Prozessinstanz gelöscht", success)
          this.closeDetailView()
        },
        error => {
          console.error("Prozessinstanz konnte nicht gelöscht werden.", error);

        }
      )
  }
}
