import {BaseEventContract} from "./base_contracts/BaseEventContract";

export interface EventContract extends BaseEventContract {
    projectId: string
    eventId: string
    orderNumber: number
}