/**
 * Stores a representation of a sprint and handles saving of updates.
 *
 * In the future this store will also likely listen for and handle update notifications from the server.
 */
import {SprintContract} from "../../../contract/SprintContract";
import {DatetimeUtils} from "../../../util/DatetimeUtils";
import {action, makeObservable, observable, runInAction} from "mobx";
import {
  LoadingDone,
  LoadingError,
  LoadingNotYetAttempted,
  LoadingPending,
  LoadingStatus
} from "../../../util/network/loading_status";
import {BaseSprintContract} from "../../../contract/base_contracts/BaseSprintContract";
import {handleErrorResponse} from "../../../util/network/network_error_handler";
import {CommentStore} from "./CommentStore";


/**
 * Store that contains all the client state for a Sprint and handles saving of updates.
 *
 * When a save causes the orderNumber of the sprint to change, a callback is triggered (if one is registered).
 */
/*{
  "id": 1,
    "post_userId": 4,
    "name": "Exam Announcement",
    "groupName": "A New Group",
    "time": "2022-08-20T10:00:00",
    "content": "This is my first post",
    "highFives": 5,
    "comments": [{
  "name": "James Potter",
  "time": "2022-08-20T10:30:00",
  "content": "Wow you posted before me"
}*/
export class PostStore {
  readonly postId: number
  readonly userId: number
  readonly postName: string
  readonly groupName: string
  readonly postTimestamp: string
  readonly postContent: string
  readonly highFives: number
  readonly comments: CommentStore[]

  protected onOrderNumberUpdate: VoidFunction | undefined

  saveSprintStatus: LoadingStatus = new LoadingNotYetAttempted()

  orderNumber: number
  startDate: Date
  endDate: Date

  constructor(sprint: SprintContract) {
    makeObservable(this, {
      saveSprintStatus: observable,
      orderNumber: observable,
      startDate: observable,
      endDate: observable,
      colour: observable,

      setDates: action
    })

    this.id = sprint.sprintId
    this.name = sprint.name
    this.colour = sprint.colour

    this.loadSelfFromJson(sprint)
  }

  protected loadSelfFromJson(sprint: SprintContract) {
    this.startDate = DatetimeUtils.networkStringToLocalDate(sprint.startDate)
    this.endDate = DatetimeUtils.networkStringToLocalDate(sprint.endDate)

    const orderNumberChanged = this.orderNumber !== sprint.orderNumber
    this.orderNumber = sprint.orderNumber
    if (this.onOrderNumberUpdate !== undefined && orderNumberChanged) {
      this.onOrderNumberUpdate()
    }
  }

  /**
   * Set the callback function to be called when a save causes the sprint to update its orderNumber.
   */
  setOrderNumberUpdateCallback(listener: VoidFunction) {
    this.onOrderNumberUpdate = listener
  }

  /**
   * Saves the new startDate and endDate, updating the orderNumber.
   * Throws if an error occurs.
   *
   * If no update is necessary, will immediately succeed.
   *
   * @param startDate to set
   * @param endDate to set
   */
  async setDates(startDate: Date, endDate: Date) {
    if (DatetimeUtils.areEqual(this.startDate, startDate) && DatetimeUtils.areEqual(this.endDate, endDate)) {
      return
    }
    if (this.saveSprintStatus instanceof LoadingPending) {
      throw new Error("A save is already in progress. Please until it is complete before trying again.")
    }

    this.saveSprintStatus = new LoadingPending()

    const newValue: BaseSprintContract = {
      name: this.name,
      description: this.description,
      startDate: DatetimeUtils.localToNetworkString(startDate),
      endDate: DatetimeUtils.localToNetworkString(endDate),
      colour: this.colour
    }

    try {
      const res = await fetch(`../api/v1/sprints/${this.id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(newValue)
      })

      if (!res.ok) {
        await handleErrorResponse(res, {
          primaryContext: "save sprint dates",
          secondaryContext: "Please try again later."
        })
      }

      const data: SprintContract = await res.json()
      runInAction(() => {
        this.loadSelfFromJson(data)
        this.saveSprintStatus = new LoadingDone()
      })
    }
    catch (e) {
      runInAction(() => {
        this.saveSprintStatus = new LoadingError(e)
      })
      throw e
    }
  }
}