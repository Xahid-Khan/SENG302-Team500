package nz.ac.canterbury.seng302.identityprovider.database;

import javax.persistence.*;

@Entity
public class GroupMemberModel {
    @Id
    private int groupId;

    @Column(nullable = false)
    private int userId;

    protected GroupMemberModel() {}

    public GroupMemberModel(int groupId, int userId) {
        this.groupId = groupId;
        this.userId = userId;
    }

    public int getGroupId() {
        return groupId;
    }

    public int getUserId() {
        return userId;
    }
}
