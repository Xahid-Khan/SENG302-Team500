import {BaseSprintContract} from "./base_contracts/BaseSprintContract";

export interface SprintContract extends BaseSprintContract {
    projectId: string
    sprintId: string
    orderNumber: number
}