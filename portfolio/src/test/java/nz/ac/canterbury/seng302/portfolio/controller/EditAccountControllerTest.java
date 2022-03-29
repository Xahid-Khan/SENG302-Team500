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
import nz.ac.canterbury.seng302.portfolio.service.AuthStateService;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountService;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

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

    @MockBean private UserAccountService userAccountService;

    @MockBean private AuthStateService authStateService;

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
    private String submitEditAccount(User user) {
        // Creates the Post Body to be sent to the mock /edit endpoint
        String postBody = buildPostBody(user);

        // Creates a mock for the /edit_account endpoint
        when(service.updateDetails(any(), any()))
                .thenReturn(
                        EditUserResponse.newBuilder()
                                .setIsSuccess(true)
                                .setMessage("Mock executed successfully")
                                .build());

        // Mocks getting the users id out of authstate
        when(authStateService.getId(any()))
                .thenReturn(1);

        return postBody;
    }

    // Helper function for extrapolating if the request was invalid.
    private boolean wasError(MvcResult result) throws Exception {
        // If there was an error, then form-error should appear at least once.
        return result.getResponse().getContentAsString().contains("form-error");
    }


    /**
     * A simple test to ensure that the Thymeleaf template is not broken for the edit account form.
     *
     * @throws Exception if perform fails for some reason
     */
    @Test
    void getEditAccountForm() throws Exception {

        // Creates a mock for the /edit_account endpoint
        when(userAccountService.getUserById(anyInt()))
                .thenReturn(
                        UserResponse.newBuilder()
                                .build());

        // Mocks getting the users id out of authstate
        when(authStateService.getId(any()))
                .thenReturn(1);

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
    void editInvalidNames(String firstName, String middleNames, String lastName) throws Exception {
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

        String postBody = submitEditAccount(user);

        var result = this.mockMvc
                .perform(
                        post(API_PATH)
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(postBody))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
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
    void editBoundaryNames(String firstName, String middleNames, String lastName) throws Exception {
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

        String postBody = submitEditAccount(user);

        var result = this.mockMvc
                .perform(
                        post(API_PATH)
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(postBody))
                .andExpect(status().is3xxRedirection())
                .andReturn();

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
    void editInvalidAdditionalInfo(String nickname, String bio, String personalPronouns) throws Exception {
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

        String postBody = submitEditAccount(user);

        var result = this.mockMvc
                .perform(
                        post(API_PATH)
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(postBody))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

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
    void editBoundaryAdditionalInfo(String nickname, String bio, String personalPronouns) throws Exception {
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

        String postBody = submitEditAccount(user);

        var result = this.mockMvc
                .perform(
                        post(API_PATH)
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(postBody))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        assertFalse(wasError(result));
    }

    /**
     * Tests a blank email. Unfortunately email is quite hard to test, so we have to assume that JavaX
     *  has the correct regex for it. The valid email test falls under the valid user test.
     */
    @Test
    void editEmptyEmail() throws Exception {
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

        String postBody = submitEditAccount(user);

        var result = this.mockMvc
                .perform(
                        post(API_PATH)
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(postBody))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        assertTrue(wasError(result));
    }
}
