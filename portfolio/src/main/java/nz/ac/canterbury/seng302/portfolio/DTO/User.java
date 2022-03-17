package nz.ac.canterbury.seng302.portfolio.DTO;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;

    @NotBlank(message = "Username cannot be blank")
    private String username;
    private String password;

    @NotBlank(message = "First name cannot be blank")
    private String firstName;

    private String middleName;

    @NotBlank(message = "Last name cannot be blank")
    private String lastName;

    private String nickname;
    private String bio;
    private String pronouns;
    private String email;

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
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User User = (User) o;
        return Objects.equals(username, User.username) && Objects.equals(password, User.password) && Objects.equals(firstName, User.firstName) && Objects.equals(middleName, User.middleName) && Objects.equals(lastName, User.lastName) && Objects.equals(nickname, User.nickname) && Objects.equals(bio, User.bio) && Objects.equals(pronouns, User.pronouns) && Objects.equals(email, User.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, firstName, middleName, lastName, nickname, bio, pronouns, email);
    }
}
