package nz.ac.canterbury.seng302.portfolio.mapping;

import nz.ac.canterbury.seng302.portfolio.model.contract.PostContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.basecontract.BasePostContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.PostEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Mapper for posts from JSON to entity and vice versa. */
@Component
public class PostMapper implements Mappable<PostEntity, BasePostContract, PostContract> {
  @Autowired private CommentMapper commentMapper;

  /**
   * Maps a contract to an entity.
   *
   * @param contract the contract (ideally a base contract) to convert to an entity
   * @return the entity representation of a contract
   */
  @Override
  public PostEntity toEntity(BasePostContract contract) {
    return new PostEntity(
        contract.groupId(), contract.userId(), contract.postContent(), contract.postTitle());
  }

  /**
   * Maps an entity to a contract.
   *
   * @param entity the entity to map to a contract
   * @return the contract representation of an entity
   */
  @Override
  public PostContract toContract(PostEntity entity) {
    return new PostContract(
        entity.getId(),
        entity.getGroupId(),
        entity.getUserId(),
        entity.getPostContent(),
        entity.getPostTitle(),
        entity.getCreated(),
        entity.getUpdated(),
        entity.getComments().stream()
            .map(commentEntity -> commentMapper.toContract(commentEntity))
            .toList());
  }
}
