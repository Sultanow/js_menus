export interface CamundaProcessInstance {
  links?: Object[],
  id: string,
  definitionId?: string,
  businessKey?: string,
  caseInstanceId?: string,
  ended: boolean,
  suspended?: boolean,
  tenantId?: string
}
