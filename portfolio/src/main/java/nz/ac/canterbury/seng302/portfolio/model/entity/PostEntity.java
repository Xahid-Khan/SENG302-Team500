package nz.ac.canterbury.seng302.portfolio.model.entity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/** Database schema for a post. */
@Entity
@Table(name = "post")
public class PostEntity extends PortfolioEntity {
  @OneToMany(mappedBy = "post", fetch = FetchType.EAGER)
  @OrderBy("created")
  @Fetch(value = FetchMode.SUBSELECT)
  private final List<CommentEntity> comments = new ArrayList<>();

  @Column(name = "group_id", nullable = false)
  private int groupId;

  @Column(name = "user_id", nullable = false)
  private int userId;

  @Column(name = "post_content", length = 4096)
  private String postContent;

  @Column(name = "post_title", length = 128)
  private String postTitle;
  // Makes the database automatically create the timestamp when the user is inserted
  @CreationTimestamp private Timestamp created;
  @CreationTimestamp private Timestamp updated;

  protected PostEntity() {}

  /**
   * Creates a new PostEntity.
   *
   * @param groupId the group's ID to tie this post to
   * @param userId the user's ID to tie this post to
   * @param postContent the content of the post
   */
  public PostEntity(int groupId, int userId, String postContent, String postTitle) {
    this.groupId = groupId;
    this.userId = userId;
    this.postContent = postContent;
    this.postTitle = postTitle;

    Date date = new Date();
    this.created = new Timestamp(date.getTime());
  }

  public Timestamp getCreated() {
    return this.created;
  }

  public Timestamp getUpdated() {
    return this.updated;
  }

  public String getPostContent() {
    return postContent;
  }

  public void setPostContent(String postContent) {
    this.postContent = postContent;
    Date date = new Date();
    this.updated = new Timestamp(date.getTime());
  }

  public String getPostTitle() {
    return postTitle;
  }

  public void setPostTitle(String postTitle) {
    this.postTitle = postTitle;
    Date date = new Date();
    this.updated = new Timestamp(date.getTime());
  }

  /**
   * Returns if the post has ever been updated in regard to content or not.
   *
   * @return if the post has been updated or not
   */
  public boolean isPostUpdated() {
    return this.updated != null;
  }

  public int getUserId() {
    return userId;
  }

  public int getGroupId() {
    return groupId;
  }

  public void addComment(CommentEntity comment) {
    comments.add(comment);
    comment.setPost(this);
  }

  public void removeComment(CommentEntity comment) {
    comments.remove(comment);
    comment.setPost(null);
  }

  public List<CommentEntity> getComments() {
    return this.comments;
  }
}
