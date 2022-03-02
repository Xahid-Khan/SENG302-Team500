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
    private String name;
    private String description;
    private Instant startDate;
    private Instant endDate;

    protected Sprint() {}

    public Sprint(long orderNumber, String name, String description, Instant startDate, Instant endDate, Project project) {
        this.orderNumber = orderNumber;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.project = project;
    }

    @Override
    public String toString() {
        return String.format(
                "Sprint [id=%d, orderNumber=%d, projectId=%d]",
                id, orderNumber, project.getId()
        );
    }

}
