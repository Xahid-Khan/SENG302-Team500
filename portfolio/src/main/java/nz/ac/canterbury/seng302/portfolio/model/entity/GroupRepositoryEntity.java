package nz.ac.canterbury.seng302.portfolio.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Represents group repositories in the database
 */
@Entity
@Table(name = "group_repository")
public class GroupRepositoryEntity extends PortfolioEntity {


    @Column(name = "group_id")
    private int groupId;

    @Column(name = "repository_id")
    private int repositoryId;

    @Column(name = "token")
    private String token;

    protected GroupRepositoryEntity() {

    }

    /**
     * Creates a new GroupRepositoryEntity with just a group ID
     * @param groupId The ID of the group associated
     */
    public GroupRepositoryEntity(int groupId) {
        this.groupId = groupId;
    }

    /**
     * Creates a new GroupRepositoryEntity with a group ID, token and a repository ID
     * @param groupId The ID of the group associated
     * @param repositoryId Groups repository ID
     * @param token The token used to access the repository
     */
    public GroupRepositoryEntity(int groupId, int repositoryId, String token) {
        this.groupId = groupId;
        this.repositoryId = repositoryId;
        this.token = token;
    }

    public int getGroupId() {
        return groupId;
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