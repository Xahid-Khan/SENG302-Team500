import {BaseCommentContract} from "./base_contracts/BaseCommentContract";

export interface CommentContract extends BaseCommentContract {
  userName: string
  commentTimestamp: string
}