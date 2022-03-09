package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.mapping.ProjectMapper;
import nz.ac.canterbury.seng302.portfolio.mapping.SprintMapper;
import nz.ac.canterbury.seng302.portfolio.model.contract.BaseProjectContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.BaseSprintContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.ProjectContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.SprintContract;
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

    @Autowired
    private ValidationService validationService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private SprintMapper sprintMapper;

    @Autowired
    private SprintRepository sprintRepository;

    @Test
    public void TestCheckAddProject() {

        BaseProjectContract project = new BaseProjectContract("test project",
                "testing",
                Instant.parse("2021-12-03T10:15:30.00Z"),
                Instant.parse("2021-12-05T10:15:30.00Z"));

        String response = validationService.checkAddProject(project);
        assertEquals("Okay", response);

        project = new BaseProjectContract("",
                "testing",
                Instant.parse("2021-12-03T10:15:30.00Z"),
                Instant.parse("2021-12-05T10:15:30.00Z"));
        response = validationService.checkAddProject(project);
        assertEquals("Project must have a name", response);
    }

    @Test
    public void TestCheckBaseFields() {

        String response = validationService.checkBaseFields("Project",
        "test project",
                "testing",
                Instant.parse("2021-12-03T10:15:30.00Z"),
                Instant.parse("2021-12-05T10:15:30.00Z"));
        assertEquals("Okay", response);

        response = validationService.checkBaseFields("Project",
                "",
                "test desc",
                Instant.parse("2021-12-03T10:15:30.00Z"),
                Instant.parse("2021-12-05T10:15:30.00Z"));
        assertEquals("Project must have a name", response);

        response = validationService.checkBaseFields("Sprint",
                "Sprint$Two",
                "test desc",
                Instant.parse("2021-12-03T10:15:30.00Z"),
                Instant.parse("2021-12-05T10:15:30.00Z"));
        assertEquals("Sprint name cannot contain any special characters", response);

        response = validationService.checkBaseFields("Sprint",
                "Sprint Two",
                "test desc",
                Instant.parse("2021-12-03T10:15:30.00Z"),
                Instant.parse("2021-12-01T10:15:30.00Z"));
        assertEquals("Sprint start date must be earlier than the end date", response);

        response = validationService.checkBaseFields("Sprint",
                "Sprint Two",
                "test desc",
                Instant.parse("2020-12-03T10:15:30.00Z"),
                Instant.parse("2021-12-01T10:15:30.00Z"));

        assertEquals("Sprint cannot start more than one year ago from today", response);
    }

    @Test
    public void TestCheckUpdateProject() {
        ProjectContract project = new ProjectContract("123",
                "test project",
                "testing",
                Instant.parse("2021-12-03T10:15:30.00Z"),
                Instant.parse("2021-12-05T10:15:30.00Z"),
                new ArrayList<SprintContract>().stream().toList());
        String response = validationService.checkUpdateProject("randomId", project);
        assertEquals("Project ID does not exist", response);

        ProjectEntity newProject = new ProjectEntity("test project",
                "testing",
                Instant.parse("2021-12-03T10:15:30.00Z"),
                Instant.parse("2021-12-05T10:15:30.00Z"));
        projectRepository.save(newProject);

        response = validationService.checkUpdateProject(newProject.getId(), projectMapper.toContract(newProject));
        assertEquals("Okay", response);

        newProject = new ProjectEntity("",
                "testing",
                Instant.parse("2021-12-03T10:15:30.00Z"),
                Instant.parse("2021-12-05T10:15:30.00Z"));
        projectRepository.save(newProject);

        response = validationService.checkUpdateProject(newProject.getId(), projectMapper.toContract(newProject));
        assertEquals("Project must have a name", response);
    }

    @Test
    public void TestCheckAddSprint() {

        ProjectEntity project = new ProjectEntity("Test project",
                "testing",
                Instant.parse("2021-12-03T10:15:30.00Z"),
                Instant.parse("2021-12-05T10:15:30.00Z"));
        String projectId = project.getId();

        SprintEntity testSprint = new SprintEntity("test sprint",
                "test desc",
                Instant.parse("2021-12-03T10:15:30.00Z"),
                Instant.parse("2021-12-05T10:15:30.00Z"));
        project.addSprint(testSprint);
        projectRepository.save(project);
        sprintRepository.save(testSprint);
        BaseSprintContract sprint = new BaseSprintContract("test sprint",
                "testing",
                Instant.parse("2021-12-03T10:15:30.00Z"),
                Instant.parse("2021-12-05T10:15:30.00Z"));



        String response = validationService.checkAddSprint(project.getId(), sprint);
        assertEquals("Okay", response);

        sprint = new BaseSprintContract("",
                "testing",
                Instant.parse("2021-12-03T10:15:30.00Z"),
                Instant.parse("2021-12-05T10:15:30.00Z"));
        response = validationService.checkAddSprint(project.getId(), sprint);
        assertEquals("Sprint must have a name", response);

        sprint = new BaseSprintContract("Test Sprint",
                "testing",
                Instant.parse("2021-12-02T10:15:30.00Z"),
                Instant.parse("2021-12-05T10:15:30.00Z"));
        response = validationService.checkAddSprint(project.getId(), sprint);
        assertEquals("Sprint cannot start before project start date", response);

        response = validationService.checkAddSprint("fakeId", sprint);
        assertEquals("Project ID does not exist", response);
    }

    @Test
    public void TestCheckUpdateSprint() {

        SprintEntity sprint = new SprintEntity("Test Sprint",
                "test desc",
                Instant.parse("2021-12-03T10:15:30.00Z"),
                Instant.parse("2021-12-05T10:15:30.00Z"));

        ProjectEntity project = new ProjectEntity("",
                "testing",
                Instant.parse("2021-12-03T10:15:30.00Z"),
                Instant.parse("2021-12-05T10:15:30.00Z"));

        project.addSprint(sprint);
        projectRepository.save(project);

        String response = validationService.checkUpdateSprint("FakeId", sprintMapper.toContract(sprint, 1));
        assertEquals("Sprint ID does not exist", response);

        sprintRepository.save(sprint);
        response = validationService.checkUpdateSprint(sprint.getId(), sprintMapper.toContract(sprint, 1));
        assertEquals("Okay", response);

        sprint = new SprintEntity("Test Sprint",
                "test desc",
                Instant.parse("2021-12-01T10:15:30.00Z"),
                Instant.parse("2021-12-05T10:15:30.00Z"));
        project.addSprint(sprint);
        sprintRepository.save(sprint);
        response = validationService.checkUpdateSprint(sprint.getId(), sprintMapper.toContract(sprint, 1));
        assertEquals("Sprint cannot start before project start date", response);

        sprint = new SprintEntity("",
                "test desc",
                Instant.parse("2021-12-03T10:15:30.00Z"),
                Instant.parse("2021-12-05T10:15:30.00Z"));
        project.addSprint(sprint);
        sprintRepository.save(sprint);
        response = validationService.checkUpdateSprint(sprint.getId(), sprintMapper.toContract(sprint, 1));
        assertEquals("Sprint must have a name", response);

        SprintContract fakeSprint = new SprintContract(project.getId(),
                "fakeId",
                "test sprint",
                "test desc",
                Instant.parse("2021-12-03T10:15:30.00Z"),
                Instant.parse("2021-12-05T10:15:30.00Z"),
                1L);
        project.addSprint(sprint);
        sprintRepository.save(sprint);
        response = validationService.checkUpdateSprint(sprint.getId(), fakeSprint);
        assertEquals("Given path ID and sprint contract ID are not the same", response);

    }

    @Test
    public void TestCheckSprintDetails() {
        ProjectContract project = new ProjectContract("123",
                "test project",
                "testing",
                Instant.parse("2021-12-03T10:15:30.00Z"),
                Instant.parse("2021-12-05T10:15:30.00Z"),
                new ArrayList<SprintContract>().stream().toList());

        Instant startDate = Instant.parse("2021-12-03T10:15:30.00Z");
        Instant endDate = Instant.parse("2021-12-05T10:15:30.00Z");

        String response = validationService.checkSprintDetails(project, "", startDate, endDate);
        assertEquals("Okay", response);

        startDate = Instant.parse("2021-12-02T10:15:30.00Z");
        response = validationService.checkSprintDetails(project, "", startDate, endDate);
        assertEquals("Sprint cannot start before project start date", response);
        startDate = Instant.parse("2021-12-03T10:15:30.00Z");

        endDate = Instant.parse("2021-12-06T10:15:30.00Z");
        response = validationService.checkSprintDetails(project, "", startDate, endDate);
        assertEquals("Sprint cannot end after project end date", response);
        endDate = Instant.parse("2021-12-05T10:15:30.00Z");


        SprintContract sprint = new SprintContract("test project id",
                "test sprint",
                "test sprint",
                "test desc",
                Instant.parse("2021-12-03T10:15:30.00Z"),
                Instant.parse("2021-12-04T10:15:30.00Z"),
                1L);
        List<SprintContract> sprints = new ArrayList<SprintContract>();
        sprints.add(sprint);
        project = new ProjectContract("123",
                "test project",
                "testing",
                Instant.parse("2021-12-03T10:15:30.00Z"),
                Instant.parse("2021-12-05T10:15:30.00Z"),
                sprints.stream().toList());
        startDate = Instant.parse("2021-12-04T00:00:30.00Z");
        response = validationService.checkSprintDetails(project, "", startDate, endDate);
        assertEquals("Sprint cannot begin while another sprint is still in progress", response);

    }
}
