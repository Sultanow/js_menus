import { ReleaseVariable } from "./release-variable";

export interface CamundaTask {
  id: string,
  name: string,
  created: string,
  processInstanceId: string,
  taskDefinitionKey: string,
  variables?: Map<string, ReleaseVariable>
}
