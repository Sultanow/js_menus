import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CamundaApiConnectorService } from '../../../services/releasemanagement/camunda-api-connector.service';
import { Router } from '@angular/router';
import { VersionDataMockService } from 'src/app/services/releasemanagement/version-data-mock.service';
import { ReleaseVariable } from 'src/app/model/release-variable';

@Component({
  selector: 'app-create-new-release',
  templateUrl: './create-new-release.component.html',
  styleUrls: ['./create-new-release.component.css']
})
export class CreateNewReleaseComponent implements OnInit {

  newReleaseForm: FormGroup;
  loading = false;
  camundaProcessInstanceId = ''
  currentVersions = []

  constructor(
    private formbuilder: FormBuilder,
    private camunda: CamundaApiConnectorService,
    private notify: MatSnackBar,
    private router: Router,
    private versions: VersionDataMockService
  ) { }

  startVariables: Map<string, ReleaseVariable> = new Map();

  ngOnInit(): void {
    this.currentVersions = this.versions.getVersions();

    this.startVariables.set("prv_release", { type: "String", value: "", valueInfo: null})
    this.startVariables.set("entwicklungsbranch", { type: "String", value: "", valueInfo: null})
    this.startVariables.set("branch_bezeichnung", { type: "String", value: "", valueInfo: null})
    this.startVariables.set("geplanter_lieferzeitpunkt_an_test", { type: "String", value: "", valueInfo: null})
    this.startVariables.set("notizen", { type: "String", value: "", valueInfo: null })

    this.newReleaseForm = this.formbuilder.group({ //idea: pull default parameters dynamically from external data source / camunda?
      prv_release: ['', Validators.required],
      entwicklungsbranch: ['', Validators.required],
      branch_bezeichnung: ['', Validators.required],
      geplanter_lieferzeitpunkt_an_test: ['', Validators.required],
      notizen: ''
    })


    //this.newReleaseForm.valueChanges.subscribe(console.log)

  }

  submitNewReleaseForm() {

    if (this.newReleaseForm.valid) {
      this.loading = true;

      //convert input variables to ReleaseVariables
      for (let key of this.startVariables.keys()) {
        let updatedVariable = this.startVariables.get(key)
        updatedVariable.value = this.newReleaseForm.value[key]
        this.startVariables.set(key, updatedVariable)
      }

      this.camunda.createNewReleaseWithVariables(this.startVariables)
        .subscribe(response => {
          if (response.ok) {
            this.loading = false;

            console.log("Sie haben eine neue Lieferung erstellt.", this.newReleaseForm.value)

            this.notify.open(`Lieferung PRV${this.newReleaseForm.value.prv_release}.${this.newReleaseForm.value.entwicklungsbranch} ${this.newReleaseForm.value.branch_bezeichnung} erstellt!`,
              null,
              {
              duration: 3000,
              verticalPosition: 'bottom',
              horizontalPosition: 'center'
              })


            this.router.navigateByUrl("/releases")

          } else {

            console.error("Lieferung konnte nicht erstellt werden.")

            this.notify.open(`Lieferung koonnnte nicht erstellt werden, bitte prüfen Sie die eingegebenen Werte. ${response.statusText}`,
              null,
              {
              duration: 3000,
              verticalPosition: 'bottom',
              horizontalPosition: 'center'
              })
          }
        })
    }
  }
}
