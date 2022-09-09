package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.authentication.PortfolioPrincipal;
import nz.ac.canterbury.seng302.portfolio.model.contract.CommentReactionContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.PostReactionContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.basecontract.BaseCommentReactionContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.basecontract.BasePostReactionContract;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateService;
import nz.ac.canterbury.seng302.portfolio.service.ReactionService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;

public class ReactionController extends AuthenticatedController {
  @Autowired
  private ReactionService highFiveService;

  public ReactionController(AuthStateService authStateService,
      UserAccountService userAccountService) {
    super(authStateService, userAccountService);
  }

  @PostMapping(value = "/post_high_five")
  public ResponseEntity<?> addPostHighFive(@AuthenticationPrincipal PortfolioPrincipal principal, BasePostReactionContract reactionContract) {
    try {
      int userId = getUserId(principal);
      PostReactionContract postReactionContract = new PostReactionContract(reactionContract.postId(), userId);
      highFiveService.processPostHighFive(postReactionContract);
      return ResponseEntity.status(HttpStatus.CREATED).build();
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.internalServerError().build();
    }
  }

  @PostMapping(value = "/comment_high_five")
  public ResponseEntity<?> addCommentHighFive(@AuthenticationPrincipal PortfolioPrincipal principal, BaseCommentReactionContract reactionContract) {
    try {
      int userId = getUserId(principal);
      CommentReactionContract commentReactionContract = new CommentReactionContract(
          reactionContract.PostId(), userId, reactionContract.commentId());
      highFiveService.processCommentHighFive(commentReactionContract);
      return ResponseEntity.status(HttpStatus.CREATED).build();
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.internalServerError().build();
    }
  }
}
