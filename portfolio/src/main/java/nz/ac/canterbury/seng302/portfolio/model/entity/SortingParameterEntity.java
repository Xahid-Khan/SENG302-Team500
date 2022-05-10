package nz.ac.canterbury.seng302.portfolio.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import nz.ac.canterbury.seng302.portfolio.model.GetPaginatedUsersOrderingElement;

@Entity
@Table(name="parameters")
public class SortingParameterEntity {
  @Id
  private int id;

  @Column
  private String  sortAttribute;

  @Column
  private boolean sortOrder;

  protected SortingParameterEntity() {};

  public SortingParameterEntity(int id, String sort, boolean order) {
    this.id = id;
    this.sortAttribute = sort;
    this.sortOrder = order;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getSortAttribute() {
    return sortAttribute;
  }

  public void setSortAttribute(String sortAttribute) {
    this.sortAttribute = sortAttribute;
  }

  public boolean isSortOrder() {
    return sortOrder;
  }

  public void setSortOrder(boolean sortOrder) {
    this.sortOrder = sortOrder;
  }
}
