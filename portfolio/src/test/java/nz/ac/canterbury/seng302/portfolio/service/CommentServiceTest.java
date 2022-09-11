//package nz.ac.canterbury.seng302.portfolio.service;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//
//import java.util.List;
//import java.util.NoSuchElementException;
//import java.util.Optional;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//import nz.ac.canterbury.seng302.portfolio.mapping.CommentMapper;
//import nz.ac.canterbury.seng302.portfolio.mapping.PostMapper;
//import nz.ac.canterbury.seng302.portfolio.model.contract.CommentContract;
//import nz.ac.canterbury.seng302.portfolio.model.contract.basecontract.BaseCommentContract;
//import nz.ac.canterbury.seng302.portfolio.model.contract.basecontract.BasePostContract;
//import nz.ac.canterbury.seng302.portfolio.model.entity.CommentEntity;
//import nz.ac.canterbury.seng302.portfolio.model.entity.PostEntity;
//import nz.ac.canterbury.seng302.portfolio.repository.CommentRepository;
//import nz.ac.canterbury.seng302.portfolio.repository.PostRepository;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest
//class CommentServiceTest {
//  private final BaseCommentContract testComment = new BaseCommentContract(5, "New test comment");
//  @InjectMocks private CommentService commentService;
//  @Mock private CommentRepository mockCommentRepository;
//  @Mock private PostRepository mockPostRepository;
//  @Autowired private CommentMapper commentMapper;
//  @Autowired private PostMapper postMapper;
//  private PostEntity post1;
//  private PostEntity post2;
//  private List<CommentEntity> commentList;
//
//  @BeforeEach
//  void setup() {
//    mockCommentRepository.deleteAll();
//    mockPostRepository.deleteAll();
//
//    post1 = postMapper.toEntity(new BasePostContract(1, 1, "This is a post"));
//    post2 = postMapper.toEntity(new BasePostContract(2, 2, "This is a second post"));
//
//    BaseCommentContract comment1 = new BaseCommentContract(1, "This is a cool post");
//    BaseCommentContract comment2 = new BaseCommentContract(3, "This is a comment");
//    BaseCommentContract comment3 = new BaseCommentContract(2, "This is new Comment to a post");
//    BaseCommentContract comment4 = new BaseCommentContract(100, "Another comment");
//
//    // Save the comments in the post, then save the post
//    Stream.of(comment1, comment2, comment3, comment4)
//        .forEach(commentContract -> post1.addComment(commentMapper.toEntity(commentContract)));
//    mockPostRepository.save(post1);
//
//    commentList =
//        Stream.of(comment1, comment2, comment3, comment4)
//            .map(commentContract -> commentMapper.toEntity(commentContract))
//            .collect(Collectors.toList());
//
//    mockCommentRepository.saveAll(commentList);
//
//    Mockito.when(mockCommentRepository.save(any())).thenReturn(null);
//    Mockito.doNothing().when(mockCommentRepository).delete(any());
//    Mockito.when(mockPostRepository.save(any())).thenReturn(null);
//  }
//
//  // ----- GET TESTS -----
//  /**
//   * Gets a created comment from the repository, and checks that getting from the service produces
//   * identical results.
//   */
//  @Test
//  void getCreatedCommentTest() {
//    // Get a comment from the list of comments.
//    CommentEntity firstComment = commentList.get(0);
//    Mockito.when(mockCommentRepository.findById(firstComment.getId()).orElseThrow())
//        .thenReturn(firstComment);
//
//    CommentContract commentContract = commentService.getCommentById(firstComment.getId());
//
//    assertEquals(commentContract, commentMapper.toContract(firstComment));
//  }
//
//  /** Ensures that NoSuchElementException is thrown when a comment ID does not exist. */
//  @Test
//  void getInvalidCommentTestIdDoesNotExist() {
//    assertThrows(NoSuchElementException.class, () -> commentService.getCommentById("-1"));
//  }
//
//  // ----- CREATE TESTS -----
//  /** Runs a test by creating a new valid comment and saving it. */
//  @Test
//  void createValidCommentTest() {
//    Mockito.when(mockPostRepository.findById(post2.getId()).orElseThrow()).thenReturn(post2);
//
//    commentService.createComment(post2.getId(), testComment);
//
//    // Check the post has 4 comments still, and the second post has 1.
////    assertEquals(4, post1.getComments().size());
////    assertEquals(1, post2.getComments().size());
//  }
//
//  /** Runs a test by ensuring that if a post does not exist, a NoSuchElementException is thrown. */
//  @Test
//  void createInvalidCommentTestIdDoesNotExist() {
//    assertThrows(
//        NoSuchElementException.class, () -> commentService.createComment("-1", testComment));
//  }
//
//  // ----- DELETE TESTS -----
//  /** Deletes a valid comment and ensures everything updates correctly. */
//  @Test
//  void deleteValidCommentTest() {
//    // Get a comment from the list of comments.
//    CommentEntity firstComment = commentList.get(0);
//    Mockito.when(mockCommentRepository.findById(any(String.class)).orElseThrow()).thenReturn(firstComment);
//
//    commentService.deleteComment(firstComment.getId());
//
//    assertEquals(3, post1.getComments().size());
//    assertEquals(3, ((List<CommentEntity>) mockCommentRepository.findAll()).size());
//  }
//
//  /**
//   * Runs a test by ensuring that if a comment does not exist, a NoSuchElementException is thrown.
//   */
//  @Test
//  void deleteInvalidCommentTestIdDoesNotExist() {
//    assertThrows(NoSuchElementException.class, () -> commentService.deleteComment("-1"));
//  }
//
//  // ----- UPDATE TESTS -----
//  @Test
//  void updateValidCommentTest() {
//    // Get a comment from the list of comments.
//    CommentEntity firstComment = commentList.get(0);
//
//    Mockito.when(mockCommentRepository.findById(commentList.get(0).getId()).orElseThrow())
//        .thenReturn(commentList.get(0));
//
//    var result = commentService.updateComment(firstComment.getId(), testComment);
//
//    Assertions.assertNotNull(result);
//    assertEquals(testComment.comment(), result.comment());
//  }
//
//  /**
//   * Runs a test by ensuring that if a comment does not exist, a NoSuchElementException is thrown.
//   */
//  @Test
//  void updateInvalidCommentTestIdDoesNotExist() {
//    assertThrows(
//        NoSuchElementException.class, () -> commentService.updateComment("-1", testComment));
//  }
//}
