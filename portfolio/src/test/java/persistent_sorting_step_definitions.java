import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.portfolio.model.GetPaginatedUsersOrderingElement;
import nz.ac.canterbury.seng302.portfolio.model.entity.SortingParameterEntity;
import nz.ac.canterbury.seng302.portfolio.repository.SortingParametersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
public class persistent_sorting_step_definitions {

  @Autowired
  SortingParameterEntity sortingEntity;

  @Autowired
  SortingParametersRepository sortingParametersRepository;

  @Autowired
  private MockMvc mockMvc;


  @Given("a user has {string} and {int} saved to their user account")
  public void aUserHasAndSavedToTheirUserAccount(String sortBy, int order) {
    boolean sortingOrder = order == 1 ? true : false;
    GetPaginatedUsersOrderingElement sortAttributes = GetPaginatedUsersOrderingElement.valueOf(sortBy);
    SortingParameterEntity newEntity = new SortingParameterEntity(1, sortAttributes, sortingOrder);

    sortingParametersRepository.save(newEntity);

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
