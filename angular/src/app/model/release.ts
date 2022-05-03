import { CamundaTask } from "./camunda-task";
import { ReleaseVariable } from "./release-variable";

export interface Release {
  processInstanceId: string,
  processDefinitionId?: string
  variables?: Map<string, ReleaseVariable>,
  tasks?: CamundaTask[]
}
