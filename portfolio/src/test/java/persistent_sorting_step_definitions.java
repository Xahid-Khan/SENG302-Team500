import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.portfolio.repository.SortingParametersRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class persistent_sorting_step_definitions {

  @Autowired
  SortingParametersRepository sortingParametersRepository;


  @Given("there is a user who is logged in")
  public void givenThereIsAUserWhoIsLoggedIn() {

  }

  @And("they have {string} saved to their user account")
  public void theyHaveParametersSavedToTheirUserAccount(String parameters) {
  }

  @When("the user loads the list of users page")
  public void theUserLoadsTheListOfUsersPage() {

  }
  @And("the user tries to filter the list of users")
  public void theUserTriesToFilterTheListOfUsers() {

  }

  @Then("the filtering should be applied")
  public void theFilteringShouldBeApplied() {

  }

  @And("the database should reflect the changes")
  public void theDatabaseShouldReflectTheChanges() {

  }
}
