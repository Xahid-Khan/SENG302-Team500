/**
 * Root store for the MonthlyPlannerPage. Handles the lifecycle of retrieving a single project (from the page URL).
 */
import {ProjectStore} from "./ProjectStore";
import {
    LoadingDone,
    LoadingError,
    LoadingNotYetAttempted,
    LoadingPending,
    LoadingStatus
} from "../../../util/network/loading_status";
import {action, makeObservable, observable, runInAction} from "mobx";
import {handleErrorResponse} from "../../../util/network/network_error_handler";

export class MonthlyPlannerPageStore {
    readonly projectId: string | undefined

    project: ProjectStore | undefined
    projectLoadingStatus: LoadingStatus = new LoadingNotYetAttempted()

    constructor() {
        makeObservable(this, {
            project: observable,
            projectLoadingStatus: observable,

            fetchProject: action
        })

        try {
            this.projectId = MonthlyPlannerPageStore.parseProjectIdFromUrl()
        }
        catch (e) {
            this.projectLoadingStatus = new LoadingError(e)
            return
        }

        this.fetchProject()
    }

    static parseProjectIdFromUrl() {
        const splitPath = window.location.pathname.split("/")
        return splitPath[splitPath.length - 1]
    }

    async fetchProject() {
        if (this.projectId === undefined || this.projectLoadingStatus instanceof LoadingPending) {
            return
        }

        this.projectLoadingStatus = new LoadingPending()
        this.project = undefined

        try {
            const res = await fetch(`/api/v1/projects/${this.projectId}`)

            if (!res.ok) {
                await handleErrorResponse(res, {
                    primaryContext: "fetch project",
                    secondaryContext: "Please try again later."
                })
            }

            const data = await res.json()
            runInAction(() => {
                this.projectLoadingStatus = new LoadingDone()
                this.project = new ProjectStore(data)
            })
        }
        catch (e) {
            runInAction(() => {
                this.projectLoadingStatus = new LoadingError(e)
            })
        }
    }
}

