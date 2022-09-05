package nz.ac.canterbury.seng302.portfolio.model.entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * Represents group repositories in the database
 */
@Entity
@Table(name = "repository")
public class GroupRepositoryEntity extends PortfolioEntity {

    @Id
    private int id = -1;

    @Column(name = "repository_id")
    private int repositoryId= -1;

    @Column(name = "token")
    private String token="No token";

    protected GroupRepositoryEntity() {

    }

    /**
     * Creates a new GroupRepositoryEntity with just a group ID
     * @param groupId The ID of the group associated
     */
    public GroupRepositoryEntity(int groupId) {
        this.id = groupId;
    }

    /**
     * Creates a new GroupRepositoryEntity with a group ID, token and a repository ID
     * @param groupId The ID of the group associated
     * @param repositoryId Groups repository ID
     * @param token The token used to access the repository
     */
    public GroupRepositoryEntity(int groupId, int repositoryId, String token) {
        this.id = groupId;
        this.repositoryId = repositoryId;
        this.token = token;
    }

    public int getRepositoryID() {
        return repositoryId;
    }

    public void setRepositoryID(int repositoryId) {
        this.repositoryId = repositoryId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


}