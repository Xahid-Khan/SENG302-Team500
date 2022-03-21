package nz.ac.canterbury.seng302.portfolio.DTO;

import javax.annotation.Nullable;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * A user in the context of the portfolio is used for validation and transportation to the IDP.
 * This record stores all attributes about the user, validates them using Javax.validation, and
 *  sends them off to the IDP to put in the database.
 *
 * Please note that all @Nullable fields may indeed be null, such as if a POST request is made
 *  without the fields in mind, such as: `curl IP:PORT/register -X POST`. As such, both a null AND
 *  empty check should be held for fields such as the Bio when choosing to display.
 *
 * Whilst *technically* the required features could be null, the @NotBlank() decorator will catch
 *  that and throw an exception anyway, so it does not matter.
 *
 * TODO: These numbers are fairly arbitrary limitations. This should be reviewed upon database
 * TODO:    constraints or similar.
 */
public record User(
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 32, message = "Username must be between 3 and 32 characters")
    String username,

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be longer than 8 characters")
    String password,

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name cannot be longer than 50 characters")
    String firstName,

    @Size(max = 50, message = "Middle name(s) cannot be longer than 50 characters")
    @Nullable String middleName,

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name cannot be longer than 50 characters")
    String lastName,

    @Size(max = 32, message = "Nickname cannot be longer than 32 characters")
    @Nullable String nickname,

    @Size(max = 512, message = "Bio cannot be longer than 512 characters")
    @Nullable String bio,

    @Size(max = 50, message = "Personal pronouns cannot be longer than 50 characters")
    @Nullable String pronouns,

    @Email(message = "Email must be valid")
    String email
) {
    // Canonical constructor to ensure that all nulls are instead filled with empty strings.
    // Null safety is important :)
    public User(
        String username,
        String password,
        String firstName,
        String middleName,
        String lastName,
        String nickname,
        String bio,
        String pronouns,
        String email
    ) {
        // Required values
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        // Optional values
        this.middleName = middleName == null ? "" : middleName;
        this.nickname = nickname == null ? "" : nickname;
        this.bio = bio == null ? "" : bio;
        this.pronouns = pronouns == null ? "" : pronouns;
    }
}
