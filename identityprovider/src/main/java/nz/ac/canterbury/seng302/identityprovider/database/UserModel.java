package nz.ac.canterbury.seng302.identityprovider.database;


import com.google.protobuf.Timestamp;

import javax.persistence.*;

@Entity
public class UserModel {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;

    @Column(unique=true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String firstName;

    private String middleName;

    @Column(nullable = false)
    private String lastName;

    private String nickname;

    private String bio;

    private String personalPronouns;

    @Column(nullable = false)
    private String email;

    private Timestamp created;

    protected UserModel() {
    }

    public UserModel(String username, String passwordHash, String firstName, String middleName, String lastName,
                     String nickname, String bio, String personalPronouns, String email, Timestamp created) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.nickname = nickname;
        this.bio = bio;
        this.personalPronouns = personalPronouns;
        this.email = email; // TODO add create account dates
        this.created = created;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getNickname() {
        return nickname;
    }

    public String getBio() {
        return bio;
    }

    public String getPersonalPronouns() {
        return personalPronouns;
    }

    public String getEmail() {
        return email;
    }

    public void setId(int id) { this.id = id; }

    public Timestamp getCreated() {return created;}

    public void setCreated(Timestamp created) {this.created = created;}

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                ", firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", nickname='" + nickname + '\'' +
                ", bio='" + bio + '\'' +
                ", personalPronouns='" + personalPronouns + '\'' +
                ", email='" + email + '\'' +
                ", created='" + created.toString() + '\'' +
                '}';
    }
}
