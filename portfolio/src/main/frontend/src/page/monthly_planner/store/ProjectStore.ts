/**
 * Store for a single (already loaded) project.
 */
import {ProjectContract} from "../../../contract/ProjectContract";
import {action, makeObservable, observable, reaction} from "mobx";
import {SprintStore} from "./SprintStore";
import {DatetimeUtils} from "../../../util/DatetimeUtils";


export class ProjectStore {
    readonly startDate: Date
    readonly endDate: Date

    sprints: SprintStore[]

    constructor(project: ProjectContract) {
        makeObservable(this, {
            sprints: observable,

            renumberSprintsFromUpdate: action
        })

        this.startDate = DatetimeUtils.networkStringToLocalDate(project.startDate)
        this.endDate = DatetimeUtils.networkStringToLocalDate(project.endDate)

        this.sprints = observable.array(project.sprints.map(sprint => {
            const sprintStore = new SprintStore(sprint)
            sprintStore.setOrderNumberUpdateCallback(() => this.renumberSprintsFromUpdate(sprintStore))
            return sprintStore
        }))
    }

    /**
     * Renumber and re-order the sprints array given that sprint has changed its orderNumber.
     *
     * Assumes that the sprints array was in order before the orderNumber update occurred.
     *
     * @param updatedSprint reference to the sprint that has had its orderNumber updated
     */
    renumberSprintsFromUpdate(updatedSprint: SprintStore) {
        const previousIndex = this.sprints.indexOf(updatedSprint)
        this.sprints.splice(previousIndex, 1)

        const newIndex = updatedSprint.orderNumber - 1
        this.sprints.splice(newIndex, 0, updatedSprint)

        for (let i=0; i < this.sprints.length; i ++) {
            if (this.sprints[i].id !== updatedSprint.id) {
                this.sprints[i].orderNumber = i + 1
            }
        }
    }
}