package nz.ac.canterbury.seng302.portfolio.controller;

import java.util.Optional;
import nz.ac.canterbury.seng302.portfolio.authentication.PortfolioPrincipal;
import nz.ac.canterbury.seng302.portfolio.model.contract.CommentContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.CommentModel;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateService;
import nz.ac.canterbury.seng302.portfolio.service.CommentService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

public class CommentController extends AuthenticatedController {

  @Autowired
  private CommentService commentService;


  protected CommentController(
      AuthStateService authStateService,
      UserAccountService userAccountService) {
    super(authStateService, userAccountService);
  }

  @PostMapping(value = "/group_feed/add_comment", produces = "application/json")
  public ResponseEntity<?> addNewComment(@AuthenticationPrincipal PortfolioPrincipal principal,
      @RequestBody CommentContract newComment) {
    try {
      int userId = getUserId(principal);
      if (newComment.userId() == userId) {
        commentService.addNewCommentsToAPost(newComment);
        return ResponseEntity.status(HttpStatus.CREATED).build();
      } else {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
      }
    } catch (Exception e) {
      return ResponseEntity.internalServerError().build();
    }
  }

  @DeleteMapping(value = "/group_feed/delete_comment/{id}", produces = "application/json")
  public ResponseEntity<?> deleteCommentById(@AuthenticationPrincipal PortfolioPrincipal principal,
      @RequestBody CommentContract comment, @PathVariable String id) {
    try {
      int commentId = Integer.parseInt(id);
      int userId = getUserId(principal);
      Optional<CommentModel>  commentRetrieved = commentService.getCommentById(commentId);
      if (commentRetrieved.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
      if (userId == commentRetrieved.get().getUserId() || isTeacher(principal)) {
          commentService.deleteCommentById(commentId);
          return ResponseEntity.ok().build();
      } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }
    } catch (Exception e) {
      return ResponseEntity.internalServerError().build();
    }
  }

  @PutMapping(value = "/group_feed/update_comment/{id}")
  public ResponseEntity<?> updateComment(@AuthenticationPrincipal PortfolioPrincipal principal, @RequestBody CommentContract updatedComment, @PathVariable String id) {
    try {
      int commentId = Integer.parseInt(id);
      int userId = getUserId(principal);

      if (updatedComment.comment().length() < 1 || updatedComment.comment().length() > 4096) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Comment cannot be more than 4096 characters");
      }
      Optional<CommentModel> commentRetrieved = commentService.getCommentById(commentId);
      if (commentRetrieved.isEmpty() || userId != commentRetrieved.get().getUserId()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }
      commentService.updateAComment(commentId, updatedComment);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      return ResponseEntity.internalServerError().build();
    }
  }
}
