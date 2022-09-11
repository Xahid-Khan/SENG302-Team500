//package nz.ac.canterbury.seng302.portfolio.service;
//
//import java.util.List;
//import java.util.NoSuchElementException;
//import java.util.Optional;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//import nz.ac.canterbury.seng302.portfolio.mapping.PostMapper;
//import nz.ac.canterbury.seng302.portfolio.model.contract.PostContract;
//import nz.ac.canterbury.seng302.portfolio.model.entity.PostEntity;
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
//class GroupFeedServiceTest {
//  @InjectMocks private PostService postService;
//  @Mock private PostRepository mockPostRepository;
//  @Autowired private PostMapper postMapper;
//  @Mock private CommentService commentService;
//  private List<PostEntity> postList;
//
//  @BeforeEach
//  public void setup() {
//    mockPostRepository.deleteAll();
//    PostContract newPost1 = new PostContract(1, 1, "A test Post by User 1");
//    PostContract newPost2 = new PostContract(1, 2, "A test Post by User 2");
//    PostContract newPost3 = new PostContract(2, 1, "A test Post by User 1 Again");
//    PostContract newPost4 = new PostContract(3, 5, "A test Post by User 5");
//
//    postList =
//        Stream.of(newPost1, newPost2, newPost3, newPost4)
//            .map(postContract -> postMapper.toEntity(postContract))
//            .collect(Collectors.toList());
//
//    mockPostRepository.saveAll(postList);
//  }
//
//  /** This tests that service can retrieve all the posts in the database. */
//  @Test
//  void getAllPostsExpectPass() {
//    Mockito.when(mockPostRepository.findAll()).thenReturn(postList);
//    var result = postService.getAllPosts();
//    for (int i = 0; i < result.size(); i++) {
//      Assertions.assertEquals(postList.get(i).getGroupId(), result.get(i).getGroupId());
//      Assertions.assertEquals(postList.get(i).getUserId(), result.get(i).getUserId());
//      Assertions.assertEquals(postList.get(i).getPostContent(), result.get(i).getPostContent());
//    }
//  }
//
//  /**
//   * This tests that A user should be able to see the posts made by a specific group. it retrieves
//   * all the posts made by a group and returns them as a list.
//   */
//  @Test
//  void getAllPostsForGivenGroupId() {
//    int groupId = 1;
//    var newPostList =
//        postList.stream()
//            .filter(postModel -> postModel.getGroupId() == groupId)
//            .collect(Collectors.toList());
//    Mockito.when(mockPostRepository.findPostByGroupId(groupId)).thenReturn(newPostList);
//
//    var result = postService.getAllPostsForGroup(groupId);
//    Assertions.assertTrue(result.size() > 0);
//    for (int i = 0; i < result.size(); i++) {
//      Assertions.assertEquals(newPostList.get(i).getId(), result.get(i).getId());
//      Assertions.assertEquals(newPostList.get(i).getGroupId(), result.get(i).getGroupId());
//      Assertions.assertEquals(newPostList.get(i).getPostContent(), result.get(i).getPostContent());
//    }
//  }
//
//  /** If there is no post with the given ID, the function will rerun null. */
//  @Test
//  void getAPostWithPostIdThatDoesNotExistExpectFail() {
//    String postId = "100";
//    Mockito.when(mockPostRepository.findById(postId)).thenThrow(new NoSuchElementException());
//    var result = postService.getPostById(postId);
//    Assertions.assertNull(result);
//  }
//
//  /** Delete a post with a given ID, if it passes it will return true and false otherwise. */
//  @Test
//  void deleteAPostWithAValidPostIdExpectPass() {
//    Mockito.when(mockPostRepository.findById(postList.get(1).getId()))
//        .thenReturn(Optional.ofNullable(postList.get(1)));
//    Mockito.when(commentService.deleteAllCommentByPostId(postList.get(1).getId())).thenReturn(true);
//    var result = postService.deletePost(postList.get(1).getId());
//    Assertions.assertTrue(result);
//  }
//
//  /** Delete a post with the given ID that doesn't exist, method will return false. */
//  @Test
//  void deleteAPostThatDoesNotExistExpectFail() {
//    Assertions.assertFalse(postService.deletePost("1000"));
//  }
//
//  /**
//   * create a new post with all the valid data for the fields, and it passes the test as expected.
//   */
//  @Test
//  void createANewPostWithValidParamsExpectPass() {
//    Mockito.when(mockPostRepository.save(postList.get(0))).thenReturn(postList.get(0));
//    PostContract postContract = postMapper.toContract(postList.get(0));
//
//    var result = postService.createPost(postContract);
//    Assertions.assertTrue(result);
//  }
//
//  /**
//   * create a post where the post content is empty, it should fail because post content is
//   * mandatory.
//   */
//  @Test
//  void createANewPostWithInvalidParamsExpectFail() {
//    PostContract postContract = new PostContract(0, 1, "");
//    var result = postService.createPost(postContract);
//    Assertions.assertFalse(result);
//  }
//
//  /**
//   * Update a post content, it's the same user who made the post, so they can update it
//   * successfully, hence it passes as expected.
//   */
//  @Test
//  void updateAPostExpectPass() {
//    Mockito.when(mockPostRepository.findById(postList.get(0).getId()))
//        .thenReturn(Optional.ofNullable(postList.get(0)));
//    PostContract postUpdate = new PostContract(1, 1, "This is An UPDATED post");
//    postList.get(0).setPostContent(postUpdate.postContent());
//    Mockito.when(mockPostRepository.save(postList.get(0))).thenReturn(postList.get(0));
//    Assertions.assertTrue(postService.updatePost(postUpdate, postList.get(0).getId()));
//  }
//
//  /**
//   * Checks the timestamp on updated posts, ensuring that the creation timestamp does not change
//   * however the updating timestamp does.
//   */
//  @Test
//  void checkTimeStampOnUpdatedPosts() {
//    PostContract testPost1 = new PostContract(1, 3, "this is a post");
//    PostEntity post1Model = postMapper.toEntity(testPost1);
//
//    Assertions.assertNull(post1Model.getUpdated());
//    Assertions.assertFalse(post1Model.isPostUpdated());
//
//    var timeBeforeChange = post1Model.getCreated();
//    post1Model.setPostContent("This is an updated post...");
//    var timeAfterChange = post1Model.getCreated();
//
//    Assertions.assertNotNull(timeBeforeChange);
//    Assertions.assertNotNull(timeAfterChange);
//    Assertions.assertEquals(timeBeforeChange, timeAfterChange);
//
//    Assertions.assertNotNull(post1Model.getUpdated());
//    Assertions.assertTrue(post1Model.isPostUpdated());
//  }
//}
