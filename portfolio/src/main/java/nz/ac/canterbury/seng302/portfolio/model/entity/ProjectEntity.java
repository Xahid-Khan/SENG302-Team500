package nz.ac.canterbury.seng302.portfolio.model.entity;

import java.util.Collections;
import java.util.Comparator;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * The database representation of a Project.
 *
 * <p>
 *     Pair this with {@link nz.ac.canterbury.seng302.portfolio.repository.ProjectRepository} to
 *     read and write instances of this to the database.
 * </p>
 */
@Entity
@Table(name = "project")
public class ProjectEntity {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1024)
    private String description;

    @Column(nullable = false)
    private Instant startDate;

    @Column(nullable = false)
    private Instant endDate;

    @OneToMany(mappedBy = "project", fetch = FetchType.EAGER)
    @OrderBy("startDate")
    @Fetch(value = FetchMode.SUBSELECT)
    private List<SprintEntity> sprints = new ArrayList<>();

    @OneToMany(mappedBy = "project", fetch = FetchType.EAGER)
    @OrderBy("startDate")
    @Fetch(value = FetchMode.SUBSELECT)
    private List<EventEntity> events = new ArrayList<>();

    @OneToMany(mappedBy = "project", fetch = FetchType.EAGER)
    @OrderBy("startDate")
    @Fetch(value = FetchMode.SUBSELECT)
    private List<MilestoneEntity> milestones = new ArrayList<>();

    @OneToMany(mappedBy = "project", fetch = FetchType.EAGER)
    @OrderBy("startDate")
    @Fetch(value = FetchMode.SUBSELECT)
    private List<DeadlineEntity> deadlines = new ArrayList<>();


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
                "Project[id=%s, name=%s]",
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

    public List<EventEntity> getEvents() {return events;}

    public List<DeadlineEntity> getDeadlines() {return deadlines;}

    public List<MilestoneEntity> getMilestones() {
        return milestones;
    }

    /**
     * Inserts the given sprint to the sprints list, retaining sorted order.
     *
     * <p>Adapted from: https://stackoverflow.com/a/51893026</p>
     *
     * @param sprint to insert
     */
    public void addSprint(SprintEntity sprint) {
        var index = Collections.binarySearch(sprints, sprint, Comparator.comparing(SprintEntity::getStartDate));
        if (index < 0) {
            index = -index - 1;
        }

        sprints.add(index, sprint);
        sprint.setProject(this);
    }

    public void removeSprint(SprintEntity sprint) {
        sprints.remove(sprint);
        sprint.setProject(null);
    }

   public void newEvent(EventEntity event) {
        events.add(event);
        event.setProject(this);
    }

    public void removeEvent(EventEntity event) {
        events.remove(event);
        event.setProject(null);
    }

    public void newDeadline(DeadlineEntity deadline) {
        deadlines.add(deadline);
        deadline.setProject(this);
    }

    public void addDeadline(DeadlineEntity deadline) {
        deadlines.add(deadline);
        deadline.setProject(this);
    }

    public void removeDeadline(DeadlineEntity deadline) {
        deadlines.remove(deadline);
        deadline.setProject(null);
    }

    /**
     * Inserts the given milestone to the milestones list, retaining sorted order.
     *
     * <p>Adapted from: https://stackoverflow.com/a/51893026</p>
     *
     * @param milestone to insert
     */
    public void addMilestone(MilestoneEntity milestone) {
        var index = Collections.binarySearch(milestones, milestone, Comparator.comparing(MilestoneEntity::getStartDate));
        if (index < 0) {
            index = -index - 1;
        }
        milestones.add(index, milestone);
        milestone.setProject(this);
    }

    /**
     * Removes milestone from milestones list
     * @param milestone to delete
     */
    public void removeMilestone(MilestoneEntity milestone) {
        milestones.remove(milestone);
        milestone.setProject(null);
    }
}