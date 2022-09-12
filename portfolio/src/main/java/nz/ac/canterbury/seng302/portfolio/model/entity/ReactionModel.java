package nz.ac.canterbury.seng302.portfolio.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "reaction_model")
public class ReactionModel {

  @Id
  @Column(name = "id", nullable = false)
  private int id;

  @Column(name = "user_id", nullable = false)
  private int userId;

  @Column(name = "post_id", nullable = false)
  private int postId;

  @Column(name = "comment_id")
  private int commentId;

  protected ReactionModel() {
  }

  public ReactionModel(int userId, int postId) {
    this.userId = userId;
    this.postId = postId;
  }

  public ReactionModel(int userId, int postId, int commentId) {
    this.userId = userId;
    this.postId = postId;
    this.commentId = commentId;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getUserId() {
    return userId;
  }

  public int getPostId() {
    return postId;
  }

  public int getCommentId() {
    return this.commentId;
  }
}