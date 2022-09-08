 import {BaseMilestoneContract} from "./base_contracts/BaseMilestoneContract";

 export interface MilestoneContract extends BaseMilestoneContract {
     projectId: string
     milestoneId: string
     orderNumber: number
 }