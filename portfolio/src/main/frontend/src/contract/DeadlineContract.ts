import {BaseDeadlineContract} from "./base_contracts/BaseDeadlineContract";

export interface DeadlineContract extends BaseDeadlineContract {
    projectId: string
    deadlineId: string
    orderNumber: number
}