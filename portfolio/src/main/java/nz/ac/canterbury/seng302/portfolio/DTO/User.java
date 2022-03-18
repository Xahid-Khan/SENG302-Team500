package nz.ac.canterbury.seng302.portfolio.DTO;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Objects;


public class User {

    private final String ONLY_LETTERS_REGEX = "^[a-zA-Z ]*$";
    private final String NO_SPECIAL_CHAR_REGEX = "^[a-zA-Z0-9.\\-+=@_ ]*$";

    @NotBlank(message = "Username cannot be blank")
    @Pattern(regexp=NO_SPECIAL_CHAR_REGEX, message="Name cannot contain special characters or spaces")
    private String username;

    private String password;

    @NotBlank(message = "First name cannot be blank")
    @Pattern(regexp=ONLY_LETTERS_REGEX, message="First name must only consist of letters")
    private String firstName;

    @Pattern(regexp=ONLY_LETTERS_REGEX, message="Last name must only consist of letters")
    private String middleName;

    @NotBlank(message = "Last name cannot be blank")
    @Pattern(regexp=ONLY_LETTERS_REGEX, message="Last name must only consist of letters")
    private String lastName;

    @Pattern(regexp=NO_SPECIAL_CHAR_REGEX, message="Nickname cannot contain special characters")
    private String nickname;

    @Pattern(regexp=NO_SPECIAL_CHAR_REGEX, message="Bio cannot contain special characters")
    private String bio;

    @Pattern(regexp=NO_SPECIAL_CHAR_REGEX, message="Pronouns cannot contain special characters")
    private String pronouns;

    @Email
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
