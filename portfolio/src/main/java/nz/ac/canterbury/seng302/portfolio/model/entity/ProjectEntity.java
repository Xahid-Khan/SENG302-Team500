package nz.ac.canterbury.seng302.portfolio.model.entity;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "project")
public class ProjectEntity {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String description;

    private Instant startDate;
    private Instant endDate;

    @OneToMany(mappedBy = "project")
    private List<SprintEntity> sprints = new ArrayList<>();

    protected ProjectEntity() {}

    public ProjectEntity(String name, String description, Instant startDate, Instant endDate) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return String.format(
                "ProjectEntity[id=%d, name=%s]",
                id,
                name
        );
    }

    public Long getId() {
        return id;
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

    public List<SprintEntity> getSprints() {
        return sprints;
    }

    public void addSprint(SprintEntity sprint) {
        sprints.add(sprint);
        sprint.setProject(this);
    }

    public void removeSprint(SprintEntity sprint) {
        sprints.remove(sprint);
        sprint.setProject(null);
    }
}