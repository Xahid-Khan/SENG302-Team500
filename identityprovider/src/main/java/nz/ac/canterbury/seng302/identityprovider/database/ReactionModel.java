package nz.ac.canterbury.seng302.identityprovider.database;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "reaction_model")
public class ReactionModel {

    @Id
    @Column(name = "user_id", nullable = false)
    private int userId;

    @Id
    @Column(name = "post_id", nullable = false)
    private int postId;

    public int getUserId() {
        return userId;
    }

    public int getPostId() {
        return postId;
    }
}