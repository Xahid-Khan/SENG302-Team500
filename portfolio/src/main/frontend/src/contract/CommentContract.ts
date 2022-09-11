import {BaseCommentContract} from "./base_contracts/BaseCommentContract";

export interface CommentContract extends BaseCommentContract {
  commentId: string
  postId: string
  created: Date
}