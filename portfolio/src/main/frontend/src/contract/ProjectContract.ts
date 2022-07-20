import {BaseProjectContract} from "./BaseProjectContract";
import {SprintContract} from "./SprintContract";
import {EventContract} from "./EventContract";
import {MilestoneContract} from "./MilestoneContract";

export interface ProjectContract extends BaseProjectContract {
    id: string
    sprints: SprintContract[]
    events: EventContract[]
    milestones: MilestoneContract[]
}