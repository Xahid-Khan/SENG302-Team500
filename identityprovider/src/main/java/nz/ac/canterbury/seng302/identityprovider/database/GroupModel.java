package nz.ac.canterbury.seng302.identityprovider.database;

import javax.persistence.*;

@Entity
public class GroupModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(unique = true, nullable = false)
    private String groupName;

    private String description;

    protected GroupModel () {}

    public GroupModel (String name, String description){
        this.groupName = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getDescription() {
        return description;
    }
}
