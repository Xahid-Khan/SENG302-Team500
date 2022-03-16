package nz.ac.canterbury.seng302.portfolio;

import com.google.type.DateTime;
import nz.ac.canterbury.seng302.portfolio.model.entity.ProjectEntity;
import nz.ac.canterbury.seng302.portfolio.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.time.Instant;
import java.time.LocalDate;

@SpringBootApplication
public class PortfolioApplication {

    @Autowired
    private ProjectRepository projectRepository;


    public static void main(String[] args) {
        SpringApplication.run(PortfolioApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void addDefaultProject() {
        int counter = 0;
        for (ProjectEntity project: projectRepository.findAll()) {
            counter++;
            break;
        }
        if (counter == 0) {
            String projectName = "Project " + LocalDate.now().getYear();
            System.out.println(projectName);
            ProjectEntity defaultProject = new ProjectEntity(projectName,
                    "",
                    Instant.now(),
                    Instant.parse(LocalDate.now().plusMonths(8).atStartOfDay().toString() + ":00.00Z"));
            projectRepository.save(defaultProject);
        }
    }

}
