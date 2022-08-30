package nz.ac.canterbury.seng302.portfolio.model.entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;


/**
 * Database schema for a post
 */
@Entity
public class PostModel {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "group_id", nullable = false)
    private int groupId;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "post_content", length = 2047)
    private String postContent;

    // Makes the database automatically create the timestamp when the user is inserted
    @CreationTimestamp
    private Timestamp created;

    @ManyToOne
    @JoinColumn(name = "reaction_model_ID")
    private ReactionModel reactionModel;

    protected PostModel() {};

    public PostModel(int groupId, int userId, String postContent){
        this.groupId = groupId;
        this.userId = userId;
        this.postContent = postContent;
    }

    public ReactionModel getReactionModel() {
        return reactionModel;
    }

    public void setReactionModel(ReactionModel reactionModel) {
        this.reactionModel = reactionModel;
    }

    public Timestamp getCreated() {
        return created;
    }

    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public int getUserId() {
        return userId;
    }

    public int getGroupId() {
        return groupId;
    }

    public int getId() {
        return id;
    }
}
