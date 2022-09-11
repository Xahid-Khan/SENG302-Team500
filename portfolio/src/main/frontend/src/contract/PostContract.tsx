import {BasePostContract} from "./base_contracts/BasePostContract";
import {CommentContract} from "./CommentContract";

export interface PostContract extends BasePostContract {
  postId: string
  created: Date
  update: Date
  comments: CommentContract[]
}