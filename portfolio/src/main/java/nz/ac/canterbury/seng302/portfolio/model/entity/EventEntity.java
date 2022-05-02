package nz.ac.canterbury.seng302.portfolio.model.entity;

import java.time.LocalDateTime;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.time.Instant;


/**
 * The database representation of an Event.
 *
*/
@Entity
@Table(name = "event")
public class EventEntity {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2") // Look into this
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Instant startDate;

    @Column(nullable = false)
    private Instant endDate;

    @Column(nullable = false)
    private LocalDateTime startTime; // Unsure if LocalDateTime or Instant should be used

    @Column(nullable = false)
    private LocalDateTime endTime;

    @ManyToOne
    private ProjectEntity project;

    protected EventEntity() {}

    public EventEntity(String name, Instant startDate, Instant endDate) { // Possibly add LocalDateTime to this
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return String.format(
                "Event[id=%s, name=%s]",
                id,
                name
        );
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProjectEntity getProject() {
        return project;
    }

    public void setProject(ProjectEntity project) {
        this.project = project;
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

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndDTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

}