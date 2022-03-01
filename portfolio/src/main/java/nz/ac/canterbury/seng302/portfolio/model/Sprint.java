package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "sprint")
public class Sprint {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    private long orderNumber;

    private Instant startDate;

    private Instant endDate;

    protected Sprint() {}

    public Sprint(long orderNumber, Instant startDate, Instant endDate) {
        this.orderNumber = orderNumber;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getId() {
        return id;
    }

    public Project getProject() {
        return project;
    }

    public long getOrderNumber() {
        return orderNumber;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }
}
