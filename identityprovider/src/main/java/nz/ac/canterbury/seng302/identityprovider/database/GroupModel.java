package nz.ac.canterbury.seng302.identityprovider.database;

import javax.persistence.*;

@Entity
public class GroupModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(unique = true, nullable = false)
    private String shortName;

    private String longName;

    protected GroupModel () {}

    public GroupModel (String shortName, String longName){
        this.shortName = shortName;
        this.longName = longName;
    }

    public int getId() {
        return id;
    }

    public String getShortName() {
        return shortName;
    }

    public String getLongName() {
        return longName;
    }
}
