package nz.ac.canterbury.seng302.identityprovider.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import nz.ac.canterbury.seng302.identityprovider.IdentityProviderApplication;
import nz.ac.canterbury.seng302.identityprovider.database.UserModel;
import nz.ac.canterbury.seng302.identityprovider.database.UserRepository;
import nz.ac.canterbury.seng302.identityprovider.exceptions.IrremovableRoleException;
import nz.ac.canterbury.seng302.identityprovider.service.RoleService;
import nz.ac.canterbury.seng302.shared.identityprovider.ModifyRoleOfUserRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Class containing the step definitions for the userrole Cucumber feature
 */
@SpringBootTest
@CucumberContextConfiguration
@ContextConfiguration(
        classes = IdentityProviderApplication.class,
        loader = SpringBootContextLoader.class
)
public class UserRoleStepDefinitions {

    @Autowired
    private UserRepository userRepository;

    @Autowired private RoleService roleService;

    private Integer userId;

    void createUser() {
        userRepository.deleteAll();
        var user = new UserModel(
        "gitgud",
            "hash",
            "Jane",
            "Danger",
            "Doe",
            "gamer",
            "i am a gamer",
            "Pronouns",
            "Email@Email.Email",
            new ArrayList<UserRole>(),
            null);
        userRepository.save(user);
        userId = user.getId();
    }

    UserModel getUser(){
        return userRepository.findById(userRepository.findAll().iterator().next().getId());
    }

    @Given("a user exists")
    public void aUserExists() {
        createUser();
    }

    @Given("the user has a role {string}")
    public void theUserHasARole(String role) {
        var request = ModifyRoleOfUserRequest.newBuilder()
                .setUserId(userId)
                .setRole(UserRole.valueOf(role.toUpperCase(Locale.ROOT)))
                .build();
        this.roleService.addRoleToUser(request);
    }


    @When("the user adds a role {string}")
    public void theUserAddsARole(String role) {
        var request = ModifyRoleOfUserRequest.newBuilder()
                .setUserId(userId)
                .setRole(UserRole.valueOf(role.toUpperCase(Locale.ROOT)))
                .build();
        this.roleService.addRoleToUser(request);
    }

    @When("the user deletes a role {string}")
    public void theUserDeletesARole(String role) {
        var request = ModifyRoleOfUserRequest.newBuilder()
                .setUserId(userId)
                .setRole(UserRole.valueOf(role.toUpperCase(Locale.ROOT)))
                .build();
        try {
            this.roleService.removeRoleFromUser(request);
        }
        catch (IrremovableRoleException e) {
            // Alg
        }
    }


    @Then("the user should have a role {string}")
    public void theUserShouldHaveARole(String role) {
        assertTrue(getUser().getRoles().contains(UserRole.valueOf(role.toUpperCase(Locale.ROOT))));
    }

    @Then("the user should not have a role {string}")
    public void theUserShouldNotHaveARole(String role) {
        assertFalse(getUser().getRoles().contains(UserRole.valueOf(role.toUpperCase(Locale.ROOT))));
    }

}
