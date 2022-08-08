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

    public GroupMemberModel(int groupId, List<Integer> userIds) {
        this.id = groupId;
        this.userIds = userIds;
    }

    public int getGroupId() {
        return this.id;
    }

    public List<Integer> getUserIds() {
        return this.userIds;
    }

    public void addNewMember(int userId) {
        this.userIds.add(userId);
    }
}
