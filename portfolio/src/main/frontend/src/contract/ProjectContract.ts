import {BaseProjectContract} from "./BaseProjectContract";
import {SprintContract} from "./SprintContract";

export interface ProjectContract extends BaseProjectContract {
    id: string
    sprints: SprintContract[]
}