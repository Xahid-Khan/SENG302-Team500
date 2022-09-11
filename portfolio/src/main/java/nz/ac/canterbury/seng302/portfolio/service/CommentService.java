package nz.ac.canterbury.seng302.portfolio.service;

import java.util.NoSuchElementException;
import nz.ac.canterbury.seng302.portfolio.mapping.CommentMapper;
import nz.ac.canterbury.seng302.portfolio.model.contract.CommentContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.basecontract.BaseCommentContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.CommentEntity;
import nz.ac.canterbury.seng302.portfolio.model.entity.PostEntity;
import nz.ac.canterbury.seng302.portfolio.repository.CommentRepository;
import nz.ac.canterbury.seng302.portfolio.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** This service handles comments. */
@Service
public class CommentService {
  @Autowired private PostRepository postRepository;
  @Autowired private CommentRepository commentRepository;
  @Autowired private CommentMapper commentMapper;

  /**
   * This function will get a specific comment using the comment's ID, and return the contract of
   * it.
   *
   * @param commentId the comment's ID. Must not be null
   * @return a CommentContract
   * @throws NoSuchElementException if the ID is invalid
   */
  public CommentContract getCommentById(String commentId) {
    return commentMapper.toContract(commentRepository.findById(commentId).orElseThrow());
  }

  /**
   * Creates a comment, tying it to a post.
   *
   * @param postId the post the comment belongs to. Must not be null
   * @param newComment a BaseCommentContract
   * @return a CommentContract
   * @throws NoSuchElementException if the ID is invalid
   */
  public CommentContract createComment(String postId, BaseCommentContract newComment) {
    System.err.println("IIIIIIIIIIII");
    PostEntity post = postRepository.findById(postId).orElseThrow();
    System.err.println(post.getId());
    System.err.println("DDDDDDDDDDDDDDD");
    CommentEntity comment = commentMapper.toEntity(newComment);
    System.err.println("1");
    post.addComment(comment);
    System.err.println("2");
    commentRepository.save(comment);
    System.err.println("3");
    postRepository.save(post);
    System.err.println("4");
    return commentMapper.toContract(comment);
  }

  /**
   * This function will delete the comment by the given comment ID.
   *
   * @param commentId the comment's ID. Must not be null
   * @throws NoSuchElementException if the ID is invalid
   */
  public void deleteComment(String commentId) {
    CommentEntity comment = commentRepository.findById(commentId).orElseThrow();
    PostEntity post = comment.getPost();

    post.removeComment(comment);
    commentRepository.delete(comment);
    postRepository.save(post);
  }

  /**
   * This function will update the edited comment and save it into the database.
   *
   * @param commentId the comment's ID. Must not be null
   * @param updatedComment a BaseCommentContract to set the comment to
   * @return updated CommentContract
   * @throws NoSuchElementException if the ID is invalid
   */
  public CommentContract updateComment(String commentId, BaseCommentContract updatedComment) {
    CommentEntity comment = commentRepository.findById(commentId).orElseThrow();

    comment.setCommentContent(updatedComment.comment());
    commentRepository.save(comment);
    return commentMapper.toContract(comment);
  }
}
