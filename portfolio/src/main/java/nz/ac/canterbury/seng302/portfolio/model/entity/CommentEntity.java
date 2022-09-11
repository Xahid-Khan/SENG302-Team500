package nz.ac.canterbury.seng302.portfolio.model.entity;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/** Database schema for a comment on a post. */
@Entity
@Table(name = "comment")
public class CommentEntity extends PortfolioEntity {
  @ManyToOne(optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private PostEntity post;

  @Column(name = "user_id", nullable = false)
  private int userId;

  @Column(name = "comment_content", length = 4096)
  private String commentContent;

  // Makes the database automatically create the timestamp when the user is inserted
  @CreationTimestamp private Timestamp created;

  public CommentEntity(int userId, String comment) {
    this.userId = userId;
    this.commentContent = comment;
  }

  protected CommentEntity() {}

  public int getUserId() {
    return userId;
  }

  public Timestamp getCreated() {
    return created;
  }

  public void setCreated(Timestamp created) {
    this.created = created;
  }

  public String getCommentContent() {
    return commentContent;
  }

  public void setCommentContent(String commentContent) {
    this.commentContent = commentContent;
  }

  public PostEntity getPost() {
    return post;
  }

  public void setPost(PostEntity post) {
    this.post = post;
  }
}
