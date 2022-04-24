import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { CamundaProcessInstance } from 'src/app/model/camunda-process-instance';
import { CamundaTask } from 'src/app/model/camunda-task';
import { Release } from "src/app/model/release";
import { concatMap, map, mergeMap, toArray } from 'rxjs/operators';
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

  getAllReleases(): Observable<Release[]>{
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
            release.variables = new Map<string, ReleaseVariable>(Object.entries(variables));
            console.log("returning release", release)
            return release;
          })
        )),
        toArray()
    )
  }

  createNewReleaseWithVariables(variables: Map<string, ReleaseVariable>): Observable<HttpResponse<Object>>{
    let request_body = this.convertVariablesToCamundaRequestBody(variables);

    return this.postNewReleaseReturnInstance()
      .pipe(
        concatMap(processInstance => this.getCurrentTasksOfProcessInstance(processInstance.id)),
        concatMap(tasks => this.postTaskForm(tasks[0].id, (request_body)))
      )
  }

  getReleaseBpmnDiagram(definitionId: string): Observable<String>{
    return this.getBpmnXmlByProcessDefinitionId(definitionId)
  }

  getReleaseTasksByInstanceId(instanceId: string): Observable<CamundaTask[]>{
    return this.getCurrentTasksOfProcessInstance(instanceId)
  }

  getTaskFormByTaskId(taskId: string): Observable<string>{
    return this.getRenderedFormFromTaskId(taskId);
  }

  getTaskVariablesById(taskId: string): Observable<Map<string, ReleaseVariable>>{
    return this.getTaskVariablesFromTaskId(taskId)
      .pipe(
        map(variables => new Map<string, ReleaseVariable>(Object.entries(variables)))
      )
  }

  submitTaskFormById(taskId: string, variables: Map<string, ReleaseVariable>): Observable<Object> {
    let request_body = this.convertVariablesToCamundaRequestBody(variables)
    return this.postTaskForm(taskId, request_body)
  }

  updateVariableByName(instanceId: string, variableName: string, newValue: string): Observable<Object>{
    return this.putProcessInstanceVariable(instanceId, variableName, newValue);
  }

  deleteProcessInstanceById(instanceId: string): Observable<Object> {
    let url = this.baseUrl + `/process-instance/${instanceId}`;
    return this.http.delete(url)
  }

  getAllReleaseVariablesByInstanceId(instanceId: string): Observable<Map<string, ReleaseVariable>>{
    return this.getProcessInstanceVariables(instanceId)
      .pipe(
        map(variables => new Map<string, ReleaseVariable>(Object.entries(variables)))
      )
  }


  //#endregion

  //#region Private methods

    //Process Instance methods

  private getCurrentProcessInstances(): Observable<CamundaProcessInstance[]> {
    let url = this.baseUrl + "/process-instance/";
    return this.http.get<CamundaProcessInstance[]>(url)
  }

  private getProcessInstanceVariables(processInstanceId: string): Observable<Map<string, ReleaseVariable>> {
    let url = this.baseUrl + `/process-instance/${processInstanceId}/variables`;
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

  private postTaskForm(taskId: string, request_body: Object): Observable<HttpResponse<Object>> {
    let url = this.baseUrl + "/task/" + taskId + "/submit-form";
    return this.http.post(url, request_body, { observe: "response" })
  }

    //Task methods

  // private getCurrentTasks(): Observable<CamundaTask[]> {
  //   let url = this.baseUrl + "/task/";
  //   return this.http.get<CamundaTask[]>(url)
  // }

  private getTaskVariablesFromTaskId(taskId: string): Observable<Map<string, ReleaseVariable>> {
    let url = this.baseUrl + `/task/${taskId}/form-variables`;
    return this.http.get<Map<string, ReleaseVariable>>(url)
  }

  private getRenderedFormFromTaskId(taskId: string): Observable<string>{
    let url = this.baseUrl + `/task/${taskId}/rendered-form`;
    return this.http.get(url, {
      observe: "body",
      responseType : "text"
    })
  }

  private putProcessInstanceVariable(instanceId: string, variableName: string, newValue: string): Observable<Object>{
    let url = this.baseUrl + `/process-instance/${instanceId}/variables/${variableName}`
    return this.http.put(url, { "value": newValue, "type": "String" })
  }

    //Miscellaneous

  private convertVariablesToCamundaRequestBody(variables: Map<string, ReleaseVariable>) {
    let converted = {}

    //Converting Map to Object for REST_API consumption
    variables.forEach((value, key) => {
      converted[key] = value
    })

    return { variables: converted }
  }

  private getBpmnXmlByProcessDefinitionId(definitionId: string): Observable<string> {
    let url = this.baseUrl + `/process-definition/${definitionId}/xml`;
    return this.http.get<CamundaBpmnXmlContainer>(url, { observe: "body" })
      .pipe(
        map(response => response.bpmn20Xml)
      )
  }

  //#endregion

}
