package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.mapping.ProjectMapper;
import nz.ac.canterbury.seng302.portfolio.mapping.SprintMapper;
import nz.ac.canterbury.seng302.portfolio.model.contract.*;
import nz.ac.canterbury.seng302.portfolio.model.contract.basecontract.BaseProjectContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.basecontract.BaseSprintContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.ProjectEntity;
import nz.ac.canterbury.seng302.portfolio.model.entity.SprintEntity;
import nz.ac.canterbury.seng302.portfolio.repository.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.repository.SprintRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class ValidationServiceTest {

  @Autowired private ValidationService validationService;

  @Autowired private ProjectRepository projectRepository;

  @Autowired private ProjectMapper projectMapper;

  @Autowired private SprintMapper sprintMapper;

  @Autowired private SprintRepository sprintRepository;

  @Test
  public void TestCheckAddProject() {

    BaseProjectContract project =
        new BaseProjectContract(
            "test project",
            "testing",
            Instant.parse("2021-12-03T10:15:30.00Z"),
            Instant.parse("2021-12-05T10:15:30.00Z"));

    String response = validationService.checkAddProject(project);
    assertEquals("Okay", response);

    project =
        new BaseProjectContract(
            "",
            "testing",
            Instant.parse("2021-12-03T10:15:30.00Z"),
            Instant.parse("2021-12-05T10:15:30.00Z"));
    response = validationService.checkAddProject(project);
    assertEquals("Project name must not be empty", response);
  }

  @Test
  public void TestCheckBaseFields() {

    String response =
        validationService.checkBaseFields(
            "Project",
            "test project",
            "test description",
            Instant.parse("2021-12-03T10:15:30.00Z"),
            Instant.parse("2021-12-05T10:15:30.00Z"));
    assertEquals("Okay", response);

    response =
        validationService.checkBaseFields(
            "Project",
            "",
            "test description",
            Instant.parse("2021-12-03T10:15:30.00Z"),
            Instant.parse("2021-12-05T10:15:30.00Z"));
    assertEquals("Project name must not be empty", response);

    response =
        validationService.checkBaseFields(
            "Sprint",
            "Sprint Two",
            "test description",
            Instant.parse("2021-12-03T10:15:30.00Z"),
            Instant.parse("2021-12-01T10:15:30.00Z"));
    assertEquals("Sprint start date must be earlier than the end date", response);

    response =
        validationService.checkBaseFields(
            "Sprint",
            "Sprint Two",
            "test description",
            Instant.parse("2020-12-03T10:15:30.00Z"),
            Instant.parse("2021-12-01T10:15:30.00Z"));

    assertEquals("Sprint cannot start more than one year ago from today", response);

    response =
        validationService.checkBaseFields(
            "Sprint",
            "    ",
            "test description",
            Instant.parse("2021-12-03T10:15:30.00Z"),
            Instant.parse("2022-12-01T10:15:30.00Z"));

    assertEquals("Sprint name must not contain only whitespaces", response);
  }

  @Test
  public void TestCheckUpdateProject() {
    ProjectContract project =
        new ProjectContract(
            "123",
            "test project",
            "testing",
            Instant.parse("2021-12-03T10:15:30.00Z"),
            Instant.parse("2021-12-05T10:15:30.00Z"),
            new ArrayList<SprintContract>().stream().toList(),
            new ArrayList<EventContract>().stream().toList(),
            new ArrayList<MilestoneContract>().stream().toList(),
            new ArrayList<DeadlineContract>().stream().toList());

    String response = validationService.checkUpdateProject("randomId", project);
    assertEquals("Project ID does not exist", response);

    ProjectEntity validProject =
        new ProjectEntity(
            "Test",
            "testing",
            Instant.parse("2021-12-03T10:15:30.00Z"),
            Instant.parse("2021-12-05T10:15:30.00Z"));

    projectRepository.save(validProject);

    ProjectEntity newProject =
        new ProjectEntity(
            "",
            "testing",
            Instant.parse("2021-12-03T10:15:30.00Z"),
            Instant.parse("2021-12-05T10:15:30.00Z"));

    response =
        validationService.checkUpdateProject(
            validProject.getId(), projectMapper.toContract(newProject));
    assertEquals("Project name must not be empty", response);

    newProject =
        new ProjectEntity(
            "test project",
            "testing",
            Instant.parse("2021-12-03T10:15:30.00Z"),
            Instant.parse("2021-12-05T10:15:30.00Z"));

    response =
        validationService.checkUpdateProject(
            validProject.getId(), projectMapper.toContract(newProject));
    assertEquals("Okay", response);
    SprintEntity sprint =
        new SprintEntity(
            "test",
            "",
            Instant.parse("2021-12-03T10:15:30.00Z"),
            Instant.parse("2021-12-05T10:15:30.00Z"),
            "#fff");
    validProject.addSprint(sprint);
    sprintRepository.save(sprint);
    projectRepository.save(validProject);

    ProjectEntity invalidProject =
        new ProjectEntity(
            "    ",
            "testing",
            Instant.parse("2021-12-03T10:15:30.00Z"),
            Instant.parse("2021-12-05T10:15:30.00Z"));

    response =
        validationService.checkUpdateProject(
            validProject.getId(), projectMapper.toContract(invalidProject));
    assertEquals("Project name must not contain only whitespaces", response);

    invalidProject =
        new ProjectEntity(
            "Invalid details",
            "testing",
            Instant.parse("2021-12-04T10:15:30.00Z"),
            Instant.parse("2021-12-05T10:15:30.00Z"));
    response =
        validationService.checkUpdateProject(
            validProject.getId(), projectMapper.toContract(invalidProject));
    assertEquals("Project cannot begin after one of its sprints start date", response);

    invalidProject =
        new ProjectEntity(
            "Invalid details",
            "testing",
            Instant.parse("2021-12-03T10:15:30.00Z"),
            Instant.parse("2021-12-03T10:18:30.00Z"));

    response =
        validationService.checkUpdateProject(
            validProject.getId(), projectMapper.toContract(invalidProject));
    assertEquals("Project cannot end before one of its sprints end date", response);
  }

  @Test
  public void TestCheckAddSprint() {

    ProjectEntity project =
        new ProjectEntity(
            "Test project",
            "testing",
            Instant.parse("2021-12-03T10:15:30.00Z"),
            Instant.parse("2021-12-05T10:15:30.00Z"));
    projectRepository.save(project);

    BaseSprintContract sprint =
        new BaseSprintContract(
            "test sprint",
            "testing",
            Instant.parse("2021-12-03T10:15:30.00Z"),
            Instant.parse("2021-12-05T10:15:30.00Z"),
            "#fff000");

    String response = validationService.checkAddSprint(project.getId(), sprint);
    assertEquals("Okay", response);

    sprint =
        new BaseSprintContract(
            "",
            "testing",
            Instant.parse("2021-12-03T10:15:30.00Z"),
            Instant.parse("2021-12-05T10:15:30.00Z"),
            "#fff000");
    response = validationService.checkAddSprint(project.getId(), sprint);
    assertEquals("Sprint name must not be empty", response);

    sprint =
        new BaseSprintContract(
            "Test Sprint",
            "testing",
            Instant.parse("2021-12-02T10:15:30.00Z"),
            Instant.parse("2021-12-05T10:15:30.00Z"),
            "#fff000");
    response = validationService.checkAddSprint(project.getId(), sprint);
    assertEquals("Sprint cannot start before project start date", response);

    response = validationService.checkAddSprint("fakeId", sprint);
    assertEquals("Project ID does not exist", response);
  }

  @Test
  public void TestCheckUpdateSprint() {

    BaseSprintContract sprint =
        new BaseSprintContract(
            "Test Sprint",
            "test desc",
            Instant.parse("2021-12-03T10:15:30.00Z"),
            Instant.parse("2021-12-05T10:15:30.00Z"),
            "#fff000");

    ProjectEntity project =
        new ProjectEntity(
            "",
            "testing",
            Instant.parse("2021-12-03T10:15:30.00Z"),
            Instant.parse("2021-12-05T10:15:30.00Z"));
    SprintEntity sprintEntity = sprintMapper.toEntity(sprint);
    project.addSprint(sprintEntity);
    projectRepository.save(project);

    String response = validationService.checkUpdateSprint("FakeId", sprint);
    assertEquals("Sprint ID does not exist", response);

    sprintRepository.save(sprintEntity);
    response = validationService.checkUpdateSprint(sprintEntity.getId(), sprint);
    assertEquals("Okay", response);

    BaseSprintContract invalidSprint =
        new BaseSprintContract(
            "   ",
            "test desc",
            Instant.parse("2021-12-03T10:15:30.00Z"),
            Instant.parse("2021-12-05T10:15:30.00Z"),
            "#fff000");

    response = validationService.checkUpdateSprint(sprintEntity.getId(), invalidSprint);
    assertEquals("Sprint name must not contain only whitespaces", response);

    invalidSprint =
        new BaseSprintContract(
            "",
            "test desc",
            Instant.parse("2021-12-03T10:15:30.00Z"),
            Instant.parse("2021-12-05T10:15:30.00Z"),
            "#fff000");

    response = validationService.checkUpdateSprint(sprintEntity.getId(), invalidSprint);
    assertEquals("Sprint name must not be empty", response);

    sprintRepository.delete(sprintEntity);

    sprint =
        new BaseSprintContract(
            "Test Sprint",
            "test desc",
            Instant.parse("2021-12-01T10:15:30.00Z"),
            Instant.parse("2021-12-05T10:15:30.00Z"),
            "#fff000");
    sprintEntity = sprintMapper.toEntity(sprint);
    project.addSprint(sprintEntity);
    sprintRepository.save(sprintEntity);
    response = validationService.checkUpdateSprint(sprintEntity.getId(), sprint);
    assertEquals("Sprint cannot start before project start date", response);
    sprintRepository.delete(sprintEntity);

    sprint =
        new BaseSprintContract(
            "",
            "test desc",
            Instant.parse("2021-12-03T10:15:30.00Z"),
            Instant.parse("2021-12-04T00:00:30.00Z"),
            "#fff000");
    sprintEntity = sprintMapper.toEntity(sprint);
    project.addSprint(sprintEntity);
    sprintRepository.save(sprintEntity);
    response = validationService.checkUpdateSprint(sprintEntity.getId(), sprint);
    assertEquals("Sprint name must not be empty", response);
  }

  @Test
  public void TestCheckSprintDetails() {
    ProjectContract project =
        new ProjectContract(
            "123",
            "test project",
            "testing",
            Instant.parse("2021-12-03T10:15:30.00Z"),
            Instant.parse("2021-12-05T10:15:30.00Z"),
            new ArrayList<SprintContract>().stream().toList(),
            new ArrayList<EventContract>().stream().toList(),
            new ArrayList<MilestoneContract>().stream().toList(),
            new ArrayList<DeadlineContract>().stream().toList());

    Instant startDate = Instant.parse("2021-12-03T10:15:30.00Z");
    Instant endDate = Instant.parse("2021-12-05T10:15:30.00Z");

    String response =
        validationService.checkSprintDetails(project, "", startDate, endDate, "#000000");
    assertEquals("Okay", response);

    startDate = Instant.parse("2021-12-02T10:15:30.00Z");
    response = validationService.checkSprintDetails(project, "", startDate, endDate, "#000000");
    assertEquals("Sprint cannot start before project start date", response);
    startDate = Instant.parse("2021-12-03T10:15:30.00Z");

    endDate = Instant.parse("2021-12-06T10:15:30.00Z");
    response = validationService.checkSprintDetails(project, "", startDate, endDate, "#000000");
    assertEquals("Sprint cannot end after project end date", response);
    endDate = Instant.parse("2021-12-05T10:15:30.00Z");

    SprintContract sprint =
        new SprintContract(
            "test project id",
            "test sprint",
            "test sprint",
            "test desc",
            Instant.parse("2021-12-03T10:15:30.00Z"),
            Instant.parse("2021-12-04T10:15:30.00Z"),
            "#fff",
            1L);
    List<SprintContract> sprints = new ArrayList<SprintContract>();
    sprints.add(sprint);
    project =
        new ProjectContract(
            "123",
            "test project",
            "testing",
            Instant.parse("2021-12-03T10:15:30.00Z"),
            Instant.parse("2021-12-05T10:15:30.00Z"),
            sprints.stream().toList(),
            new ArrayList<EventContract>(),
            new ArrayList<MilestoneContract>().stream().toList(),
            new ArrayList<DeadlineContract>().stream().toList()
        );
    startDate = Instant.parse("2021-12-04T00:00:30.00Z");
    response = validationService.checkSprintDetails(project, "", startDate, endDate, "#000000");
    assertEquals("Sprint cannot begin while another sprint is still in progress", response);
  }
}
