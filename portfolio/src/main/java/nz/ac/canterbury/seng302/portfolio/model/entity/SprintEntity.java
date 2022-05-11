package nz.ac.canterbury.seng302.portfolio.model.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * The database representation of a Sprint.
 *
 * <p>
 *     Pair this with {@link nz.ac.canterbury.seng302.portfolio.repository.SprintRepository} to
 *     read and write instances of this to the database.
 * </p>
 */
@Entity
@Table(name = "sprint")
public class SprintEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "uuid2")
    private String id;

    @ManyToOne(optional = false)
    @OnDelete(action=OnDeleteAction.CASCADE)
    private ProjectEntity project;

    @Column(nullable = false)
    private String name;

    @Column(length = 1024)
    private String description;

    @Column(nullable = false)
    private Instant startDate;

    @Column(nullable = false)
    private Instant endDate;

    @Column(nullable = false)
    private String colour;

    protected SprintEntity() {}

    public SprintEntity(String name, String description, Instant startDate, Instant endDate, String colour) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.colour = colour;
    }

    @Override
    public String toString() {
        return String.format(
                "Sprint [id=%s, projectId=%s]",
                id, (this.project != null) ? project.getId() : "-1"
        );
    }

    public String getId() {
        return id;
    }

    public ProjectEntity getProject() {
        return project;
    }

    public void setProject(ProjectEntity project) {
        this.project = project;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public String getColour() {return colour;}

    public void setColour(String colour) {this.colour = colour;}

    /**
     * Calculates the orderNumber of this sprint entity by searching through its project.
     *
     * @return the orderNumber of this sprint in the project
     */
    public int getOrderNumber() {
        var sprints = project.getSprints();
        for (int i=0; i < sprints.size(); i++) {
            if (sprints.get(i).getId().equals(this.id)) {
                return i + 1;
            }
        }

        throw new IllegalStateException("this.project does not contain this sprint, so getOrderNumber is impossible.");
    }


}
