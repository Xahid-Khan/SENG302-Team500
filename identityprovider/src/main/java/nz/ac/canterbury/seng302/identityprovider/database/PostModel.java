package nz.ac.canterbury.seng302.identityprovider.database;

import javax.persistence.*;


/**
 * Database schema for a post
 */
@Entity
public class PostModel {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "high_five_count", nullable = false)
    private int highFiveCount;

    @Column(name = "group_id", nullable = false)
    private int groupId;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "post_content", length = 2047)
    private String postContent;

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

    public int getHighFiveCount() {
        return highFiveCount;
    }

    public void setHighFiveCount(int highFiveCount) {
        this.highFiveCount = highFiveCount;
    }

    public int getId() {
        return id;
    }
}
