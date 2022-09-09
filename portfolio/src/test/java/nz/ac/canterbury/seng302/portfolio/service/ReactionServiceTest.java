package nz.ac.canterbury.seng302.portfolio.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import nz.ac.canterbury.seng302.portfolio.model.contract.CommentReactionContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.PostReactionContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.ReactionModel;
import nz.ac.canterbury.seng302.portfolio.repository.ReactionModelRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ReactionServiceTest {

  @InjectMocks
  private ReactionService reactionService;
  @Mock
  private ReactionModelRepository mockReactionRepository;

  private CommentReactionContract commentReactionContract1;
  private CommentReactionContract commentReactionContract2;
  private CommentReactionContract commentReactionContract3;

  private PostReactionContract postReactionContract1;
  private PostReactionContract postReactionContract2;
  private PostReactionContract postReactionContract3;

  private List<ReactionModel> allReactions;

  @BeforeEach
  void setup() {
    mockReactionRepository.deleteAll();
    allReactions = new ArrayList<>();
    commentReactionContract1 = new CommentReactionContract(1, 1, 1);
    commentReactionContract2 = new CommentReactionContract(1, 2, 2);
    commentReactionContract3 = new CommentReactionContract(2, 3, 1);
    ReactionModel comment1 = new ReactionModel(commentReactionContract1.userId(),
        commentReactionContract1.postId(), commentReactionContract1.commentId());
    ReactionModel comment2 = new ReactionModel(commentReactionContract2.userId(),
        commentReactionContract2.postId(), commentReactionContract2.commentId());
    ReactionModel comment3 = new ReactionModel(commentReactionContract3.userId(),
        commentReactionContract3.postId(), commentReactionContract3.commentId());
    allReactions.add(comment1);
    allReactions.add(comment2);
    allReactions.add(comment3);

    postReactionContract1 = new PostReactionContract(1, 1);
    postReactionContract2 = new PostReactionContract(2, 3);
    postReactionContract3 = new PostReactionContract(1, 1);
    ReactionModel comment4 = new ReactionModel(postReactionContract1.userId(),
        postReactionContract1.postId());
    ReactionModel comment5 = new ReactionModel(postReactionContract2.userId(),
        postReactionContract2.postId());
    ReactionModel comment6 = new ReactionModel(postReactionContract3.userId(),
        postReactionContract3.postId());
    allReactions.add(comment4);
    allReactions.add(comment5);
    allReactions.add(comment6);
  }

  /**
   * Get all the reactions from the database for a user, either he has reacted to a post or a comment.
   * using the user id.
   */
  @Test
  void getAllReactionsByUserIdAndExpectPass() {
    Mockito.when(mockReactionRepository.getReactionsByUserId(1))
        .thenReturn(allReactions.stream().filter(reaction -> reaction.getUserId() == 1).collect(
            Collectors.toList()));
    var result = reactionService.getReactionsByUserId(1);
    Assertions.assertTrue(result.size() > 0);
    result.forEach(reaction -> {
      Assertions.assertEquals(1, reaction.getUserId());
    });
  }

  /**
   * Get all the reactions from the database for a post, using the post id.
   */
  @Test
  void getAllReactionsByPostIdAndExpectPass() {
    Mockito.when(mockReactionRepository.getReactionsByPostId(1))
        .thenReturn(allReactions.stream().filter(reaction -> reaction.getPostId() == 1).collect(
            Collectors.toList()));
    var result = reactionService.getReactionByPostId(1);
    Assertions.assertTrue(result.size() > 0);
    result.forEach(reaction -> {
      Assertions.assertEquals(1, reaction.getPostId());
    });
  }

  /**
   * Get all the reactions from the database for a comment, using the comment id.
   */
  @Test
  void getAllReactionsByCommentIdAndExpectPass() {
    Mockito.when(mockReactionRepository.getReactionsByCommentId(1))
        .thenReturn(allReactions.stream().filter(reaction -> reaction.getCommentId() == 1).collect(
            Collectors.toList()));
    var result = reactionService.getReactionByCommentId(1);
    Assertions.assertTrue(result.size() > 0);
    result.forEach(reaction -> {
      Assertions.assertEquals(1, reaction.getCommentId());
    });
  }

  /**
   * Get all the reactions from the database for a user, either he has reacted to a post or a comment.
   * using the user id. But the returned list will be empty because this user has not reacted to any
   * post or comments.
   */
  @Test
  void getAllReactionsByUserIdAndExpectFail() {
    Mockito.when(mockReactionRepository.getReactionsByUserId(-500))
        .thenReturn(allReactions.stream().filter(reaction -> reaction.getUserId() == -500).collect(
            Collectors.toList()));
    var result = reactionService.getReactionsByUserId(-500);
    Assertions.assertFalse(result.size() > 0);
  }

  /**
   * Get all the reactions from the database for a post, using the post id. Expect fail because
   * the post has no reaction.
   */
  @Test
  void getAllReactionsByPostIdAndExpectFail() {
    Mockito.when(mockReactionRepository.getReactionsByPostId(-500))
        .thenReturn(allReactions.stream().filter(reaction -> reaction.getPostId() == -500).collect(
            Collectors.toList()));
    var result = reactionService.getReactionByPostId(-500);
    Assertions.assertFalse(result.size() > 0);
  }

  /**
   * Get all the reactions from the database for a comment, using the post id. Expect fail because
   * the comment has no reaction.
   */
  @Test
  void getAllReactionsByCommentIdAndExpectFail() {
    Mockito.when(mockReactionRepository.getReactionsByCommentId(-500))
        .thenReturn(allReactions.stream().filter(reaction -> reaction.getCommentId() == -500).collect(
            Collectors.toList()));
    var result = reactionService.getReactionByCommentId(-500);
    Assertions.assertFalse(result.size() > 0);
  }

  /**
   * This function will test that the user can react to a post if he/she has not already reacted to
   * that post.
   */
  @Test
  void processAPostHighFiveThatUserHasNotReactedToExpectPass() {
    Mockito.when(mockReactionRepository.getReactionsByUserId(any(int.class)))
        .thenReturn(new ArrayList<>());
    Mockito.when(mockReactionRepository.save(any())).thenReturn(allReactions.get(3));

    var result = reactionService.processPostHighFive(postReactionContract1);
    Assertions.assertTrue(result);
    verify(mockReactionRepository).save(any());
  }

  /**
   * This function will test that if the user has already reacted to the post and press the high-five
   * reaction tab again it will remove the reaction from the post.
   */
  @Test
  void processAPostHighFiveIfUserAlreadyReactedThanReactingAgainWillRemoveTheReaction () {
    Mockito.when(mockReactionRepository.getReactionsByUserId(1))
        .thenReturn(allReactions.stream().filter(reaction -> reaction.getUserId() == 1).collect(
            Collectors.toList()));
    Mockito.doNothing().when(mockReactionRepository).deleteById(any());

    var result = reactionService.processPostHighFive(postReactionContract1);
    Assertions.assertTrue(result);
    verify(mockReactionRepository).deleteById(any());
  }

  /**
   * This function will test that the user can react to a comment if he/she has not already reacted to
   * that comment.
   */
  @Test
  void processACommentHighFiveThatUserHasNotReactedToExpectPass() {
    Mockito.when(mockReactionRepository.getReactionsByUserId(any(int.class)))
        .thenReturn(new ArrayList<>());
    Mockito.when(mockReactionRepository.save(any())).thenReturn(allReactions.get(0));

    var result = reactionService.processCommentHighFive(commentReactionContract1);

    Assertions.assertTrue(result);
    verify(mockReactionRepository).save(any());
  }

  /**
   * This function will test that if the user has already reacted to the comment and press the high-five
   * reaction tab again it will remove the reaction from the comment.
   */
  @Test
  void processACommentHighFiveIfUserAlreadyReactedThanReactingAgainWillRemoveTheReaction () {
    Mockito.when(mockReactionRepository.getReactionsByUserId(1))
        .thenReturn(allReactions.stream().filter(reaction -> reaction.getUserId() == 1).collect(
            Collectors.toList()));
    Mockito.doNothing().when(mockReactionRepository).deleteById(any());

    var result = reactionService.processCommentHighFive(commentReactionContract1);
    Assertions.assertTrue(result);
    verify(mockReactionRepository).deleteById(any());
  }
}
