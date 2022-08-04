package nz.ac.canterbury.seng302.identityprovider.database;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class GroupMemberModel {
    @Id
    private int groupId;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(nullable = false)
    private List <Integer> userId = new ArrayList<>();

    protected GroupMemberModel() {}

    public GroupMemberModel(int groupId, List<Integer> userId) {
        this.groupId = groupId;
        this.userId = userId;
    }

    public int getGroupId() {
        return groupId;
    }

    public List<Integer> getUserId() {
        return userId;
    }
}
