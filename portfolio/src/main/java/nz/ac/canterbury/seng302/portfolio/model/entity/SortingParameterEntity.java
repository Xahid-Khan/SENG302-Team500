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
  private GetPaginatedUsersOrderingElement sortAttribute;

  @Column
  private boolean sortOrder;
}
