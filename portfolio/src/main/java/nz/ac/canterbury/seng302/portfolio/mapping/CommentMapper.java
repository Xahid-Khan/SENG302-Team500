package nz.ac.canterbury.seng302.portfolio.mapping;

import nz.ac.canterbury.seng302.portfolio.model.contract.CommentContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.basecontract.BaseCommentContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.CommentEntity;
import org.springframework.stereotype.Component;

/** Mapper for comments from JSON to entity and vice versa. */
@Component
public class CommentMapper
    implements Mappable<CommentEntity, BaseCommentContract, CommentContract> {

  /**
   * Maps a BaseCommentContract to a CommentEntity.
   *
   * @param contract the BaseCommentContract to convert to a CommentEntity
   * @return the entity representation of the contract
   */
  @Override
  public CommentEntity toEntity(BaseCommentContract contract) {
    return new CommentEntity(contract.userId(), contract.comment());
  }

  /**
   * Maps a CommentEntity to a CommentContract.
   *
   * @param entity the CommentEntity to map to a CommentContract
   * @return the contract representation of an entity
   */
  @Override
  public CommentContract toContract(CommentEntity entity) {
    return new CommentContract(
        entity.getId(),
        entity.getUserId(),
        entity.getPost().getId(),
        entity.getCommentContent(),
        entity.getCreated());
  }
}
