/**
 * Store for a single (already loaded) project.
 */
import {ProjectContract} from "../../../contract/ProjectContract";
import {makeObservable, observable} from "mobx";
import {SprintContract} from "../../../contract/SprintContract";

export interface ProjectStoreSprint extends Omit<Omit<SprintContract, 'startDate'>, 'endDate'> {
    startDate: Date
    endDate: Date
}

export class ProjectStore {
    readonly startDate: Date
    readonly endDate: Date

    sprints: ProjectStoreSprint[]

    constructor(project: ProjectContract) {
        makeObservable(this, {
            sprints: observable
        })

        this.startDate = new Date(Date.parse(project.startDate))
        this.endDate = new Date(Date.parse(project.endDate))

        this.sprints = observable.array(project.sprints.map(sprint => ({
            ...sprint,
            startDate: new Date(Date.parse(sprint.startDate)),
            endDate: new Date(Date.parse(sprint.endDate))
        })))
    }
}