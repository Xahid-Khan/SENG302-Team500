package nz.ac.canterbury.seng302.identityprovider.database;


import com.google.protobuf.Timestamp;

import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "roles", nullable = false)
    private List<UserRole> roles = new ArrayList<>();

    protected UserModel() {
    }

    public UserModel(String username, String passwordHash, String firstName, String middleName, String lastName,
        String nickname, String bio, String personalPronouns, String email, List<UserRole> roles) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.nickname = nickname;
        this.bio = bio;
        this.personalPronouns = personalPronouns;
        this.email = email;
        this.roles = roles;
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

    public List<UserRole> getRoles() { return roles; }

    /**
     * Adds a role to a user
     *
     * @param role  The role to add
     */
    public void addRole(UserRole role) {
        roles.add(role);
    }

    /**
     * Deletes a role from a user
     *
     * @param role  The role to remove
     */
    public void deleteRole(UserRole role) {
        roles.remove(role);
    }

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
            ", roles='" + roles.toString() + '\'' +
            '}';
    }
}
