import {BaseProjectContract} from "./BaseProjectContract";
import {SprintContract} from "./SprintContract";
import {EventContract} from "./EventContract";

export interface ProjectContract extends BaseProjectContract {
    id: string
    sprints: SprintContract[]
    events: EventContract[]
}