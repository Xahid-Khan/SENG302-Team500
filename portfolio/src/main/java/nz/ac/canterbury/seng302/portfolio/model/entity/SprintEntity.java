package nz.ac.canterbury.seng302.portfolio.model.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "sprint")
public class SprintEntity {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne()
    @OnDelete(action=OnDeleteAction.CASCADE)
    private ProjectEntity project;

    private long orderNumber;
    private String name;
    private String description;
    private Instant startDate;
    private Instant endDate;

    protected SprintEntity() {}

    public SprintEntity(long orderNumber, String name, String description, Instant startDate, Instant endDate) {
        this.orderNumber = orderNumber;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return String.format(
                "SprintEntity [id=%d, orderNumber=%d, projectId=%d]",
                id, orderNumber, (this.project != null) ? project.getId() : -1
        );
    }

    public Long getId() {
        return id;
    }

    public ProjectEntity getProject() {
        return project;
    }

    public void setProject(ProjectEntity project) {
        this.project = project;
    }

    public long getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(long orderNumber) {
        this.orderNumber = orderNumber;
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
}
