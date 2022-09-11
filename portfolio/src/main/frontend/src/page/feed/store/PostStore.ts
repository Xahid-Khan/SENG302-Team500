import {CommentStore} from "./CommentStore";
import {makeObservable, observable} from "mobx";
import {PostContract} from "../../../contract/PostContract";

export class PostStore {
  readonly postId: string
  readonly userId: number
  postTitle: string
  postContent: string
  readonly postCreated: Date
  comments: CommentStore[]

  constructor(post: PostContract) {
    makeObservable(this, {
      postContent: observable,
      postTitle: observable
    })

    this.postId = post.postId;
    this.userId = post.userId;
    this.postTitle = post.postTitle;
    this.postContent = post.postContent;
    this.postCreated = post.created;
    this.comments = observable.array(post.comments.map(comment => {
      return new CommentStore(comment);
    }));
  }
}