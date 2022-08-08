package nz.ac.canterbury.seng302.identityprovider.database;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class GroupMemberModel {
    @Id
    private int id;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(nullable = false)
    private List <Integer> userIds = new ArrayList<>();

    protected GroupMemberModel() {}

    public GroupMemberModel(int groupId, List<Integer> userId) {
        this.id = groupId;
        this.userIds = userId;
    }

    public int getGroupId() {
        return this.id;
    }

    public List<Integer> getUserId() {
        return userIds;
    }

    public void addNewMember(int userId) {
        this.userIds.add(userId);
    }
}
