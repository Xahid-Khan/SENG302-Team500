package nz.ac.canterbury.seng302.portfolio.cucumber;

import io.cucumber.core.backend.CucumberBackendException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import nz.ac.canterbury.seng302.portfolio.DTO.User;
import nz.ac.canterbury.seng302.portfolio.PortfolioApplication;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateService;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.EditUserResponse;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {"pretty", "html:target/cucumber-report.html"},
        features = {"src/test/resources"}
)
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
public class EditAccountCucumberTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private RegisterClientService service;

    @MockBean
    private AuthStateService authStateService;

    private final String API_PATH = "/edit_account";

    private User user;

    private MvcResult result;

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

        //Creates a mock for the /edit_account endpoint
        when(service.updateDetails(any(), any()))
                .thenReturn(
                        EditUserResponse.newBuilder()
                                .setIsSuccess(true)
                                .setMessage("Mock executed successfully")
                                .build());

//         Mocks getting the users id out of authstate
        when(authStateService.getId(any()))
                .thenReturn(1);

        return postBody;
    }

    // Helper function for extrapolating if the request was invalid.
    private boolean wasError(MvcResult result) throws Exception {
        // If there was an error, then form-error should appear at least once.
        return result.getResponse().getContentAsString().contains("form-error");
    }

    @Given("User sets th name fields to {string}, {string}, {string}")
    public void user_sets_th_name_fields_to(String string, String string2, String string3) {
        user = new User(
                "Username",
                "Password",
                "Cody!",
                "Andrew",
                "Larsen",
                "Nickname",
                "Bio",
                "Pronouns",
                "email%40email.com",
                null);
    }
    @When("User saves the changes they have made")
    public void user_saves_the_changes_they_have_made() throws Exception {
        String postBody = submitEditAccount(user);
        result = this.mockMvc
                .perform(
                        post(API_PATH)
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(postBody))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
    }

    @Then("User details are successfully updated")
    public void user_details_are_successfully_updated() throws Exception {
        assertFalse(wasError(result));
    }

}
