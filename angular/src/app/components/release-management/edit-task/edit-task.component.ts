import { Component, Inject, OnInit } from '@angular/core';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { CamundaTask } from 'src/app/model/camunda-task';
import { ReleaseVariable } from 'src/app/model/release-variable';
import { CamundaApiConnectorService } from 'src/app/services/releasemanagement/camunda-api-connector.service';

@Component({
  selector: 'app-edit-task',
  templateUrl: './edit-task.component.html',
  styleUrls: ['./edit-task.component.css']
})
export class EditTaskComponent implements OnInit {

  //Could be used later, if Camunda Embedded/External Forms are implemented.
  // @ViewChild('taskFormHtml') taskFormHtml: ElementRef;

  isLoading: boolean = true;
  submitFormError: boolean = false;
  submitVariableError: boolean = false;

  constructor(
    public dialogReference: MatDialogRef<EditTaskComponent>,
    @Inject(MAT_DIALOG_DATA) public task: CamundaTask,
    private camunda: CamundaApiConnectorService,
    public dialog: MatDialog
  ) { }

  ngOnInit(): void {
    this.camunda.getTaskVariablesById(this.task.id)
      .subscribe(variables => {
        this.task.variables = variables
        this.isLoading = false;
      })
  }

  updateVariable(variableName: string, variableType: string, newValue: string) {
    this.isLoading = true;

    this.camunda.updateVariableByName(this.task.processInstanceId, variableName, newValue)
      .subscribe(() => {
        let newReleaseVariable: ReleaseVariable = { type: variableType, value: newValue, valueInfo: null };
        this.task.variables.set(variableName, newReleaseVariable);
        this.isLoading = false;
      },
        error => {
          this.isLoading = false;
          this.submitVariableError = true;
          console.error("Variable konnte nicht aktualisiert werden", error)
      })
  }

  submitForm() {
    this.isLoading = true;

    this.camunda.submitTaskFormById(this.task.id, this.task.variables)
      .subscribe(
        success => {
          //TODO: Add success notification
          console.log("Erfolgreich abgeschlossen", success)
          this.isLoading = false;
          this.dialogReference.close()
        },
        error => {
          //TODO: Add error notification
          this.isLoading = false;
          this.submitFormError = true;
          console.error("Fehler beim Abschließen des Prozessschritts", error)
        }
      )
  }

}
