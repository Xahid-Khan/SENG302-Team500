package nz.ac.canterbury.seng302.portfolio.DTO;

import javax.annotation.Nullable;
import javax.annotation.RegEx;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * A user in the context of the portfolio is used for validation and transportation to the IDP.
 * This record stores all attributes about the user, validates them using Javax.validation, and
 *  sends them off to the IDP to put in the database.
 *
 * TODO: These numbers are fairly arbitrary limitations. This should be reviewed upon database
 * TODO:    constraints or similar.
 */
public record User(
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 32, message = "Username must be between 3 and 32 characters")
    String username,

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
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

    // Emails are stupidly complicated. This basic Regex should suffice for most use cases however.
    @Email(message = "Email must be valid", regexp = "[^@]+@[^@]+\\.[^@.]+")
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
