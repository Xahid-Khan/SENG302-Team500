package nz.ac.canterbury.seng302.portfolio.model.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Entity
@Table(name = "event_entity")
public class EventEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "uuid2")
    private String id;

    // TODO Check validation in line with previous validation
    @Pattern(message = "Please use letters or characters", regexp = "(?<=\\s|^)[a-zA-Z]*(?=[.,;:]?\\s|$)")
    // Regex pattern from https://stackoverflow.com/questions/36851740/regex-for-just-alphabetic-characters-only-java
    @Length(message = "Name must be between 1 and 50 characters", min = 1, max = 50)
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "start_date")
    private String startDate;

    @NotNull
    @Column(name = "end_date")
    private String endDate;

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
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
}