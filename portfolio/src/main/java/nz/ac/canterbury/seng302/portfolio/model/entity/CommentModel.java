package nz.ac.canterbury.seng302.portfolio.model.entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Database schema for a comment on a post
 */
@Entity
public class CommentModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "post_id", nullable = false)
    private int postId;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "comment_content", length = 1023)
    private String commentContent;

    // Makes the database automatically create the timestamp when the user is inserted
    @CreationTimestamp
    private Timestamp created;

    public int getUserId() {
        return userId;
    }

    public Timestamp getCreated() {
        return created;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public int getPostId() {
        return postId;
    }

    public int getId() {
        return id;
    }
}
