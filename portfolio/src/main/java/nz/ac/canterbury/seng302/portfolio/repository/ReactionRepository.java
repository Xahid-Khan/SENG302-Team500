package nz.ac.canterbury.seng302.portfolio.repository;

import java.util.List;
import nz.ac.canterbury.seng302.portfolio.model.entity.ReactionEntity;
import org.springframework.data.repository.CrudRepository;

public interface ReactionRepository extends CrudRepository<ReactionEntity, Integer> {
  List<ReactionEntity> getReactionsByUserId (int userId);
  List<ReactionEntity> getReactionsByPostId (int postId);
  List<ReactionEntity> getReactionsByCommentId (int commentId);
}
