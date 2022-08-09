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
    private List<Integer> userId = new ArrayList<>();

    protected GroupMemberModel() {
    }

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



    /**
     * Adds users to the group.
     *
     * @param userIds The user ids to add.
     * @return The message if failed. Returns success if successful.
     */
    public String addUserIds(List<Integer> userIds) {
        for (Integer id : userIds) {
            if (!this.userId.contains(id)) {
                this.userId.add(id);

            } else {
                return "Error: User already in group";
            }
        }
        return "Success";
    }

    /**
     * Removes users from the group.
     *
     * @param userIds The user ids to add.
     * @return The message if failed. Returns success if successful.
     */
    public String removeUserIds(List<Integer> userIds) {
        for (Integer id : userIds) {
            if (this.userId.contains(id)) {
                this.userId.remove(id);

            } else {
                return "Error: User not in group";
            }
        }
        return "Success";

    }
}
