package nz.ac.canterbury.seng302.portfolio.DTO;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * A user in the context of the portfolio is used for validation and transportation to the IDP.
 * This record stores all attributes about the user, validates them using Javax.validation, and
 *  sends them off to the IDP to put in the database.
 */
public record User(
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 32, message = "Username must be between 3 and 32 characters")
    String username,

    @NotBlank(message = "Password is required")
    @Min(value = 8, message = "Password must be longer than 8 characters")
    String password,

    @NotBlank(message = "First name is required")
    @Max(value = 50, message = "First name cannot be longer than 50 characters")
    String firstName,

    @Max(value = 50, message = "Middle name(s) cannot be longer than 50 characters")
    String middleName,

    @NotBlank(message = "Last name is required")
    @Max(value = 50, message = "Last name cannot be longer than 50 characters")
    String lastName,

    @Max(value = 32, message = "Nickname cannot be longer than 32 characters")
    String nickname,

    @Max(value = 512, message = "Bio cannot be longer than 512 characters")
    String bio,

    @Max(value = 50, message = "Personal pronouns cannot be longer than 50 characters")
    String pronouns,

    @Email(message = "Email must be valid")
    String email
) {}
