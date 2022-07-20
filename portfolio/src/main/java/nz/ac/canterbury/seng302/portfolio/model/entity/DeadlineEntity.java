package nz.ac.canterbury.seng302.portfolio.model.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.Instant;

/**
 * Entity class for deadlines.
 */
@Entity
@Table(name = "deadline")
public class DeadlineEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "uuid2")
    private String id;

    @ManyToOne(optional = false)
    @OnDelete(action= OnDeleteAction.CASCADE)
    private ProjectEntity project;


    // TODO Check validation in line with previous validation
    @Pattern(message = "Please use letters or characters", regexp = "(?<=\\s|^)[a-zA-Z\s]*(?=[.,;:]?\\s|$)")
    // Regex pattern from https://stackoverflow.com/questions/36851740/regex-for-just-alphabetic-characters-only-java
    @Length(message = "Name must be between 1 and 50 characters", min = 1, max = 50)
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "start_date")
    private Instant startDate;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ProjectEntity getProject() {
        return project;
    }

    public void setProject(ProjectEntity project) {
        this.project = project;
    }

    public @NotNull Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(@NotNull Instant startDate) {
        this.startDate = startDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String title) {
        this.name = title;
    }

    protected DeadlineEntity() {}

    public DeadlineEntity(String name, String description, @NotNull Instant startDate) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
    }

    /**
     * Calculates the orderNumber of this deadline entity by searching through its project.
     *
     * @return the orderNumber of this deadline in the project
     */
    public int getOrderNumber() {
        var deadlines = project.getDeadlines();
        for (int i=0; i < deadlines.size(); i++) {
            if (deadlines.get(i).getId().equals(this.id)) {
                return i + 1;
            }
        }

        throw new IllegalStateException("this.project does not contain this deadline, so getOrderNumber is impossible.");
    }

}
