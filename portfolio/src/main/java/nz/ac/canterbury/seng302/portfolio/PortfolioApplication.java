package nz.ac.canterbury.seng302.portfolio;

import com.google.type.DateTime;
import nz.ac.canterbury.seng302.portfolio.model.entity.ProjectEntity;
import nz.ac.canterbury.seng302.portfolio.repository.ProjectRepository;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.time.*;

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
                    LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant(), // Instant gets the date in UTC
                    // LocalDate.now().plusMonths(8).atStartOfDay().toString() + ":00.00Z" this needs to be converted properly to UTC
                    // LocalDate will be the current system date - so this is most likely not being parsed correctly as Instant expects it in UTC
                    LocalDate.now().plusMonths(8).atStartOfDay(ZoneId.systemDefault()).toInstant());

                    //Instant.parse(LocalDate.now().plusMonths(8).atStartOfDay() + ":00.00Z"));
            projectRepository.save(defaultProject);
        }
    }

}


