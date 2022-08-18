package nz.ac.canterbury.seng302.identityprovider.database;

import javax.persistence.*;

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

    @Column(name = "comment_content", length = 1023)
    private String commentContent;

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
