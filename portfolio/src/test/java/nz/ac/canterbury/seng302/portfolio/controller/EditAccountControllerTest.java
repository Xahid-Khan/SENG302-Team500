package nz.ac.canterbury.seng302.portfolio.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import nz.ac.canterbury.seng302.portfolio.DTO.User;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.security.Principal;

/**
 * This class tests the Edit Account Controller, which is used for handling reasonable inputs on
 * what a user can edit their account details as. Note: All Null test cases are not included here as they break the
 * tests, however manual testing shows that Nulls are fine.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class EditAccountControllerTest {

    @Autowired MockMvc mockMvc;

    @MockBean private RegisterClientService service;

    private final String API_PATH = "/edit_account";

    // Helper function to place create a Post Body out of a user.
    // Note that a factory or builder pattern could've been used here,
    //  however this is just for testing.
    private String buildPostBody(User user) {
        return "username="
                + user.username()
                + "&email="
                + user.email()
                + "&password="
                + user.password()
                + "&firstName="
                + user.firstName()
                + "&middleName="
                + user.middleName()
                + "&lastName="
                + user.lastName()
                + "&nickname="
                + user.nickname()
                + "&personalPronouns="
                + user.personalPronouns()
                + "&bio="
                + user.bio()
                + "&edit_account=0";
    }

    // Helper function to submit an edit account request to a mock /edit_account endpoint.
    private MvcResult submitEditAccount(User user) throws Exception {
        // Creates the Post Body to be sent to the mock /register endpoint
        var postBody = buildPostBody(user);

        // Creates a mock for the /edit_account endpoint
        when(service.updateDetails(any(), any()))
                .thenReturn(
                        EditUserResponse.newBuilder()
                                .setIsSuccess(true)
                                .setMessage("Mock executed successfully")
                                .build());
        AuthState principal = AuthState.newBuilder().build();
        // Performs the request to the /edit_account endpoint
        Principal principal1;
        SecurityContextHolder.getContext().setAuthentication(principal);
        return this.mockMvc
                .perform(
                        post(API_PATH).contentType(MediaType.APPLICATION_FORM_URLENCODED).content(postBody).)
                .andExpect(status().is2xxSuccessful())
                .andReturn();
    }

    // Helper function for extrapolating if the request was invalid.
    private boolean wasError(MvcResult result) throws Exception {
        // If there was an error, then form-error should appear at least once.
        return result.getResponse().getContentAsString().contains("form-error");
    }


    /**
     * A simple test to ensure that the Thymeleaf template is not broken for the registration form.
     *
     * @throws Exception if perform fails for some reason
     */
    @Test
    void getEditAccountForm() throws Exception {

        // If Thymeleaf throws an exception, it will be caught via this test.
        this.mockMvc.perform(get(API_PATH)).andExpect(status().isOk());
    }

    /**
     * Tests (in order): <br>
     *  Empty first name, valid middle name(s), valid last name <br>
     *  Valid first name, valid middle name(s), empty last name <br>
     *  Invalid first name (too long), valid middle name(s), valid last name <br>
     *  Valid first name, invalid middle name(s) (too long), valid last name <br>
     *  Valid first name, valid middle name(s), invalid last name (too long) <br>
     *
     * @throws Exception if perform fails for some reason
     */
    @ParameterizedTest
    @CsvSource({
            "'',Middle Names,LastName",
            "FirstName,Middle Names,''",
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA,Middle Names,LastName",
            "FirstName,AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA,LastName",
            "FirstName,Middle Names,AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
    })
    void registerInvalidNames(String firstName, String middleNames, String lastName) throws Exception {
        var user =
                new User(
                        "Username",
                        "Password",
                        firstName,
                        middleNames,
                        lastName,
                        "Nickname",
                        "Bio",
                        "Pronouns",
                        "email%40email.com");

        var result = submitEditAccount(user);

        assertTrue(wasError(result));
    }

    /**
     * Tests (in order): <br>
     *  Size 50 first name, valid middle name(s), valid last name <br>
     *  Valid first name, Size 50 middle name(s), valid last name <br>
     *  Valid first name, valid middle name(s), Size 50 last name <br>
     *
     * @throws Exception if perform fails for some reason
     */
    @ParameterizedTest
    @CsvSource({
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA,Middle Names,LastName",
            "FirstName,AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA,LastName",
            "FirstName,Middle Names,AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
    })
    void registerBoundaryNames(String firstName, String middleNames, String lastName) throws Exception {
        var user =
                new User(
                        "Username",
                        "Password",
                        firstName,
                        middleNames,
                        lastName,
                        "Nickname",
                        "Bio",
                        "Pronouns",
                        "email%40email.com");

        var result = submitEditAccount(user);

        assertFalse(wasError(result));
    }

    /**
     * Tests (in order): <br>
     *  Invalid nickname (too long), valid bio, valid personalPronouns <br>
     *  Valid nickname, invalid bio (too long), valid personalPronouns <br>
     *  Valid nickname, valid bio, invalid personalPronouns (too long) <br>
     *
     * @throws Exception if perform fails for some reason
     */
    @ParameterizedTest
    @CsvSource({
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA,bio,personalPronouns",
            "nickname,AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA,personalPronouns",
            "nickname,bio,AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
    })
    void registerInvalidAdditionalInfo(String nickname, String bio, String personalPronouns) throws Exception {
        var user =
                new User(
                        "Username",
                        "Password",
                        "firstName",
                        "middle Names",
                        "lastName",
                        nickname,
                        bio,
                        personalPronouns,
                        "email%40email.com");

        var result = submitEditAccount(user);

        assertTrue(wasError(result));
    }

    /**
     * Tests (in order): <br>
     *  Empty nickname, valid bio, valid personalPronouns <br>
     *  Valid nickname, empty bio, valid personalPronouns <br>
     *  Valid nickname, valid bio, empty personalPronouns <br>
     *  Size 32 nickname, valid bio, valid personalPronouns <br>
     *  Valid nickname, size 512 bio, valid personalPronouns <br>
     *  Valid nickname, valid bio, size 50 personalPronouns <br>
     *
     * @throws Exception if perform fails for some reason
     */
    @ParameterizedTest
    @CsvSource({
            "'',bio,personalPronouns",
            "nickname,'',personalPronouns",
            "nickname,bio,''",
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA,bio,personalPronouns",
            "nickname,AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA,personalPronouns",
            "nickname,bio,AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
    })
    void registerBoundaryAdditionalInfo(String nickname, String bio, String personalPronouns) throws Exception {
        var user =
                new User(
                        "Username",
                        "Password",
                        "firstName",
                        "middle Names",
                        "lastName",
                        nickname,
                        bio,
                        personalPronouns,
                        "email%40email.com");

        var result = submitEditAccount(user);

        assertFalse(wasError(result));
    }

    /**
     * Tests a blank email. Unfortunately email is quite hard to test, so we have to assume that JavaX
     *  has the correct regex for it. The valid email test falls under the valid user test.
     */
    @Test
    void registerEmptyEmail() throws Exception {
        var user =
                new User(
                        "Username",
                        "Password",
                        "firstName",
                        "middle Names",
                        "lastName",
                        "nickname",
                        "bio",
                        "personalPronouns",
                        "");

        var result = submitEditAccount(user);

        assertTrue(wasError(result));
    }
}
