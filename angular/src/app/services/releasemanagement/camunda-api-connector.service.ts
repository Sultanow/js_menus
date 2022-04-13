import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { CamundaProcessInstance } from 'src/app/model/camunda-process-instance';
import { CamundaTask } from 'src/app/model/camunda-task';
import { Release } from "src/app/model/release";
import { concatMap, map, mergeMap } from 'rxjs/operators';
import { ReleaseVariable } from 'src/app/model/release-variable';
import { CamundaBpmnXmlContainer } from 'src/app/model/camunda-bpmn-xml-container';

@Injectable({
  providedIn: 'root'
})
export class CamundaApiConnectorService {
  baseUrl: string = environment.camundaApiUrl;
  releaseManagementProcessKey: string = environment.camundaReleaseManagementProcessKey;

  allReleases: Release[] = []

  constructor(private http: HttpClient) { }

  //#region Public methods

  getAllReleases(): Observable<Release>{
    return this.getCurrentProcessInstances()
      .pipe(
        mergeMap(instances => instances.map(instance => {
          return {
            processInstanceId: instance.id,
            processDefinitionId: instance.definitionId,
            taskId: instance.id,
          } as Release
        })),
        mergeMap(release => this.getProcessInstanceVariables(release.processInstanceId).pipe(
          map(variables => {
            release.variables = variables;
            return release;
          })
        ))
      )
  }

  getAllReleases_legacy(): Observable<Release> {
    return this.getCurrentTasks()
      .pipe(
        mergeMap(tasks => tasks.map(task => {
          return {
            processInstanceId: task.processInstanceId,
            taskId: task.id,
          } as Release
        })),
        mergeMap(release => this.getTaskVariablesFromTaskId(release.taskId).pipe(
          map(variables => {
            release.variables = variables;
            return release;
          })
        ))
      )
  }

  createNewReleaseWithVariables(variables): Observable<HttpResponse<Object>>{
    let request_body = this.convertVariablesToCamundaRequestBody(variables);
    console.log("converted: ", request_body)

    return this.postNewReleaseReturnInstance()
      .pipe(
        concatMap(processInstance => this.getCurrentTasksOfProcessInstance(processInstance.id)),
        concatMap(tasks => this.postSubmitTaskForm(tasks[0].id, request_body))
      )
  }

  getReleaseBpmnDiagram(definitionId: string): Observable<String>{
    return this.getBpmnXmlByProcessDefinitionId(definitionId)
  }

  getReleaseTasksByInstanceId(instanceId: string): Observable<CamundaTask[]>{
    return this.getCurrentTasksOfProcessInstance(instanceId)
  }

  //#endregion

  //#region Private methods

  private getCurrentProcessInstances(): Observable<CamundaProcessInstance[]> {
    let url = this.baseUrl + "/process-instance/";
    return this.http.get<CamundaProcessInstance[]>(url)
  }

  private getCurrentTasks(): Observable<CamundaTask[]> {
    let url = this.baseUrl + "/task/";
    return this.http.get<CamundaTask[]>(url)
  }

  private getProcessInstanceVariables(processInstanceId: string): Observable<Map<string, ReleaseVariable>> {
    let url = this.baseUrl + `/process-instance/${processInstanceId}/variables`;
    return this.http.get<Map<string, ReleaseVariable>>(url)
  }

  private getTaskVariablesFromTaskId(taskId: string): Observable<Map<string, ReleaseVariable>> {
    let url = this.baseUrl + `/task/${taskId}/form-variables`;
    return this.http.get<Map<string, ReleaseVariable>>(url)
  }

  private postNewReleaseReturnInstance(): Observable<CamundaProcessInstance> {
    let url = this.baseUrl + "/process-definition/key/" + this.releaseManagementProcessKey +  "/start";
    return this.http.post<CamundaProcessInstance>(url, {})
  }

  private getCurrentTasksOfProcessInstance(processInstanceId: string): Observable<CamundaTask[]> {
    let url = this.baseUrl + "/task" + "?processInstanceId=" + processInstanceId;
    return this.http.get<CamundaTask[]>(url)
  }

  private postSubmitTaskForm(taskId: string, request_body: Object): Observable<HttpResponse<Object>> {
    let url = this.baseUrl + "/task/" + taskId + "/submit-form";
    return this.http.post(url, request_body, { observe: "response" })

  }

  private convertVariablesToCamundaRequestBody(variables) {
    let converted = { variables: {} }

    Object.keys(variables).forEach(key => {
      converted.variables[key] = { "value": variables[key] };
    })

    return converted;
  }

  private getBpmnXmlByProcessDefinitionId(definitionId: string): Observable<String> {
    let url = this.baseUrl + `/process-definition/${definitionId}/xml`;
    return this.http.get<CamundaBpmnXmlContainer>(url, { observe: "body" })
      .pipe(
        map(response => response.bpmn20Xml)
      )
  }

  //#endregion

}
