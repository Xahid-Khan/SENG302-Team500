import {CommentContract} from "../../../contract/CommentContract";
import {makeObservable, observable} from "mobx";


export class CommentStore {
  readonly created: Date
  comment: string

  constructor(comment: CommentContract) {
    makeObservable(this, {
      comment: observable,
    })
    this.created = comment.created;
    this.comment = comment.comment;
  }
}