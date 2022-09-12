package nz.ac.canterbury.seng302.portfolio.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import nz.ac.canterbury.seng302.portfolio.authentication.PortfolioPrincipal;
import nz.ac.canterbury.seng302.portfolio.model.contract.CommentContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.PostContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.PostModel;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateService;
import nz.ac.canterbury.seng302.portfolio.service.CommentService;
import nz.ac.canterbury.seng302.portfolio.service.GroupsClientService;
import nz.ac.canterbury.seng302.portfolio.service.PostService;
import nz.ac.canterbury.seng302.portfolio.service.ReactionService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountService;
import nz.ac.canterbury.seng302.shared.identityprovider.GroupDetailsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * This is an end point controller for group posts.
 */
@Controller
public class GroupFeedController extends AuthenticatedController {

  @Autowired
  private PostService postService;

  @Autowired
  private GroupsClientService groupsClientService;

  @Autowired
  private CommentService commentService;

  @Autowired
  private UserAccountService userAccountService;

  @Autowired
  private ReactionService reactionService;

  @Value("${nz.ac.canterbury.seng302.portfolio.urlPathPrefix}")
  private String urlPathPrefix;

  public GroupFeedController(AuthStateService authStateService,
      UserAccountService userAccountService) {
    super(authStateService, userAccountService);
  }

  @GetMapping(value = "/group_feed/{groupId}", produces = "application/json")
  public String getGroupFeed(@PathVariable Integer groupId, Model model) {
    addMockDataForTesting();
    model.addAttribute("relativePath", urlPathPrefix);
    return "group_feed";
  }

  @GetMapping(value = "/feed_content/{groupId}", produces = "application/json")
  public ResponseEntity<?> getFeedContent(@PathVariable Integer groupId) {
    try {
      GroupDetailsResponse groupDetailsResponse = groupsClientService.getGroupById(groupId);
      List<PostModel> allPosts = postService.getAllPostsForAGroup(
          groupDetailsResponse.getGroupId());
      Map<String, Object> data = combineAndPrepareForFrontEnd(allPosts, groupDetailsResponse);
      return ResponseEntity.ok(data);
    } catch (NoSuchElementException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  @PostMapping(value = "/group_feed/new_post", produces = "application/json")
  public ResponseEntity<?> addNewPost(@AuthenticationPrincipal PortfolioPrincipal principal,
      @RequestBody PostContract newPost) {
    try {
      int userId = getUserId(principal);
      if (groupsClientService.isMemberOfTheGroup(userId, newPost.groupId())) {
        postService.createPost(newPost, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
      } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }
    } catch (Exception e) {
      return ResponseEntity.internalServerError().build();
    }
  }

  @DeleteMapping(value = "/delete_feed/{postId}", produces = "application/json")
  public ResponseEntity<?> deletePost(@AuthenticationPrincipal PortfolioPrincipal principal,
      @PathVariable int postId) {
    try {
      int userId = getUserId(principal);
      PostModel post = postService.getPostById(postId);
      if (isTeacher(principal) || (groupsClientService.isMemberOfTheGroup(userId, post.getGroupId())
          && userId == post.getUserId())) {
        postService.deletePost(postId);
        return ResponseEntity.ok().build();
      } else {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
      }
    } catch (Exception e) {
      return ResponseEntity.internalServerError().build();
    }
  }

  @PutMapping(value = "/update_feed/{postId}", produces = "application/json")
  public ResponseEntity<?> updatePost(@AuthenticationPrincipal PortfolioPrincipal principal,
      @PathVariable int postId, @RequestBody PostContract updatedPost) {
    try {
      int userId = getUserId(principal);
      PostModel post = postService.getPostById(postId);
      if (groupsClientService.isMemberOfTheGroup(userId, post.getGroupId())
          && userId == post.getUserId()) {
        postService.updatePost(updatedPost, postId);
        return ResponseEntity.ok().build();
      } else {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
      }
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.internalServerError().build();
    }
  }

  /**
   * This function creates a Map from posts to send it to the front end as JSON object.
   *
   * @param posts                All the posts from a group as a List
   * @param groupDetailsResponse The details of a Group
   * @return A Hash Map where first element is string and second is an object.
   */
  private Map<String, Object> combineAndPrepareForFrontEnd(List<PostModel> posts,
      GroupDetailsResponse groupDetailsResponse) {
    Map<String, Object> postWithComments = new HashMap<>();
    postWithComments.put("groupId", groupDetailsResponse.getGroupId());
    postWithComments.put("shortName", groupDetailsResponse.getShortName());

    List<Map<String, Object>> allPosts = new ArrayList<>();

    posts.forEach(post -> {
      Map<String, Object> filteredPosts = new HashMap<>();
      filteredPosts.put("postId", post.getId());
      filteredPosts.put("userId", post.getUserId());
      filteredPosts.put("username", userAccountService.getUserById(post.getUserId()).getUsername());
      filteredPosts.put("time", post.getCreated());
      filteredPosts.put("content", post.getPostContent());
      filteredPosts.put("reactions", reactionService.getUsernamesOfUsersWhoReactedToPost(
          post.getId()));
      filteredPosts.put("comments", commentService.getCommentsForThePostAsJson(post.getId()));

      allPosts.add(filteredPosts);
    });
    postWithComments.put("posts", allPosts);
    return postWithComments;
  }
  private void addMockDataForTesting() {
    if (postService.getAllPosts().size() == 0) {
      postService.createPost(new PostContract(1, "This is a test 1 post"), 3);
      postService.createPost(new PostContract(1, "This is a test 2 post"), 3);
      postService.createPost(new PostContract(1, "This is a test 3 post"), 3);
      commentService.addNewCommentsToPost(new CommentContract(3, postService.getAllPosts().get(0).getId(), "This is a comment to the post for test1."));
      commentService.addNewCommentsToPost(new CommentContract(3, postService.getAllPosts().get(1).getId(), "This is a comment to the post for test2."));
      commentService.addNewCommentsToPost(new CommentContract(3, postService.getAllPosts().get(0).getId(), "This is a comment to the post for test3."));
    }
  }
}
