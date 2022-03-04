package nz.ac.canterbury.seng302.portfolio.model.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.Instant;

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
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @ManyToOne(optional = false)
    @OnDelete(action=OnDeleteAction.CASCADE)
    private ProjectContract project;

    @Column(nullable = false)
    private long orderNumber;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String description;

    @Column(nullable = false)
    private Instant startDate;

    @Column(nullable = false)
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
                "Sprint [id=%s, orderNumber=%d, projectId=%d]",
                id, orderNumber, (this.project != null) ? project.getId() : "-1"
        );
    }

    public String getId() {
        return id;
    }

    public ProjectContract getProject() {
        return project;
    }

    public void setProject(ProjectContract project) {
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
