package nz.ac.canterbury.seng302.portfolio.DTO;

import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;

import java.util.List;
import java.util.Objects;

public class User {
    private String username;
    private String password;
    private String firstName;
    private String middleName;
    private String lastName;
    private String nickname;
    private String bio;
    private String pronouns;
    private String email;
    private List<UserRole> roles;

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getPronouns() {
        return pronouns;
    }

    public void setPronouns(String pronouns) {
        this.pronouns = pronouns;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername(){
        return username;
    }

    public void setRoles(List<UserRole> roles) { this.roles = roles; }

    public List<UserRole> getRoles() { return roles; }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", nickname='" + nickname + '\'' +
                ", bio='" + bio + '\'' +
                ", pronouns='" + pronouns + '\'' +
                ", email='" + email + '\'' +
                ", roles='" + roles.toString() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User User = (User) o;
        return Objects.equals(username, User.username) && Objects.equals(password, User.password) && Objects.equals(firstName, User.firstName) && Objects.equals(middleName, User.middleName) && Objects.equals(lastName, User.lastName) && Objects.equals(nickname, User.nickname) && Objects.equals(bio, User.bio) && Objects.equals(pronouns, User.pronouns) && Objects.equals(email, User.email) && Objects.equals(roles, User.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, firstName, middleName, lastName, nickname, bio, pronouns, email, roles);
    }
}
