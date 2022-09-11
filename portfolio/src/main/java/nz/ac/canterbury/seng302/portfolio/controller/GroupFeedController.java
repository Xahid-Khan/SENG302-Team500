//package nz.ac.canterbury.seng302.portfolio.controller;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.NoSuchElementException;
//import nz.ac.canterbury.seng302.portfolio.authentication.PortfolioPrincipal;
//import nz.ac.canterbury.seng302.portfolio.model.contract.PostContract;
//import nz.ac.canterbury.seng302.portfolio.model.entity.PostEntity;
//import nz.ac.canterbury.seng302.portfolio.service.AuthStateService;
//import nz.ac.canterbury.seng302.portfolio.service.CommentService;
//import nz.ac.canterbury.seng302.portfolio.service.GroupsClientService;
//import nz.ac.canterbury.seng302.portfolio.service.PostService;
//import nz.ac.canterbury.seng302.portfolio.service.UserAccountService;
//import nz.ac.canterbury.seng302.shared.identityprovider.GroupDetailsResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//
///** This is an end point controller for group posts. */
//@Controller
//public class GroupFeedController extends AuthenticatedController {
//
//  @Autowired private PostService postService;
//
//  @Autowired private GroupsClientService groupsClientService;
//
//  @Autowired private CommentService commentService;
//
//  @Autowired private UserAccountService userAccountService;
//
//  public GroupFeedController(
//      AuthStateService authStateService, UserAccountService userAccountService) {
//    super(authStateService, userAccountService);
//  }
//
//  /**
//   * Handles loading the group feed page.
//   *
//   * @param groupId the ID of the group to load from
//   * @return the group feed page
//   */
//  @GetMapping(value = "/group_feed/{groupId}", produces = "application/json")
//  public String getGroupFeed(@PathVariable Integer groupId) {
//    return "group_feed";
//  }
//
//  /**
//   * Handles loading the data from group feeds.
//   *
//   * @param groupId the ID of the group to load from
//   * @return the content of the feed in JSON
//   */
//  @GetMapping(value = "/feed_content/{groupId}", produces = "application/json")
//  public ResponseEntity<?> getFeedContent(@PathVariable Integer groupId) {
//    try {
//      GroupDetailsResponse groupDetailsResponse = groupsClientService.getGroupById(groupId);
//      List<PostEntity> allPosts =
//          postService.getAllPostsForGroup(groupDetailsResponse.getGroupId());
//      Map<String, Object> data = combineAndPrepareForFrontEnd(allPosts, groupDetailsResponse);
//      return ResponseEntity.ok(data);
//    } catch (NoSuchElementException e) {
//      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//    }
//  }
//
//  /**
//   * Handles POST requests for creating new posts.
//   *
//   * @param principal the authentication principal
//   * @param newPost the post to create as a PostContract
//   * @return a HTTP status code regarding the request
//   */
//  @PostMapping(value = "/group_feed/new_post", produces = "application/json")
//  public ResponseEntity<?> addNewPost(
//      @AuthenticationPrincipal PortfolioPrincipal principal, @RequestBody PostContract newPost) {
//    try {
//      int userId = getUserId(principal);
//      if (groupsClientService.isMemberOfTheGroup(userId, newPost.groupId())) {
//        postService.createPost(newPost);
//        return ResponseEntity.status(HttpStatus.CREATED).build();
//      } else {
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//      }
//    } catch (Exception e) {
//      return ResponseEntity.internalServerError().build();
//    }
//  }
//
//  /**
//   * Handles DELETE requests for posts.
//   *
//   * @param principal the authentication principal
//   * @param postId the post to delete
//   * @return a HTTP status code regarding the request
//   */
//  @DeleteMapping(value = "/delete_feed/{postId}", produces = "application/json")
//  public ResponseEntity<?> deletePost(
//      @AuthenticationPrincipal PortfolioPrincipal principal, @PathVariable String postId) {
//    try {
//      int userId = getUserId(principal);
//      PostEntity post = postService.getPostById(postId);
//      if (isTeacher(principal)
//          || (groupsClientService.isMemberOfTheGroup(userId, post.getGroupId())
//              && userId == post.getUserId())) {
//        postService.deletePost(postId);
//        return ResponseEntity.ok().build();
//      } else {
//        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//      }
//    } catch (Exception e) {
//      return ResponseEntity.internalServerError().build();
//    }
//  }
//
//  /**
//   * Handles PUT requests for updating a post.
//   *
//   * @param principal the authentication principal
//   * @param postId the post to update
//   * @param updatedPost what to update the post with
//   * @return a HTTP status code regarding the request
//   */
//  @PutMapping(value = "/update_feed/{postId}", produces = "application/json")
//  public ResponseEntity<?> updatePost(
//      @AuthenticationPrincipal PortfolioPrincipal principal,
//      @PathVariable String postId,
//      @RequestBody PostContract updatedPost) {
//    try {
//      int userId = getUserId(principal);
//      PostEntity post = postService.getPostById(postId);
//      if (groupsClientService.isMemberOfTheGroup(userId, post.getGroupId())
//          && userId == post.getUserId()) {
//        postService.updatePost(updatedPost, postId);
//        return ResponseEntity.ok().build();
//      } else {
//        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//      }
//    } catch (Exception e) {
//      e.printStackTrace();
//      return ResponseEntity.internalServerError().build();
//    }
//  }
//
//  /**
//   * This function creates a Map from posts to send it to the front end as JSON object.
//   *
//   * @param posts All the posts from a group as a List
//   * @param groupDetailsResponse The details of a Group
//   * @return A map where first element is string and second is an object.
//   */
//  private Map<String, Object> combineAndPrepareForFrontEnd(
//      List<PostEntity> posts, GroupDetailsResponse groupDetailsResponse) {
//    Map<String, Object> postWithComments = new HashMap<>();
//    postWithComments.put("groupId", groupDetailsResponse.getGroupId());
//    postWithComments.put("shortName", groupDetailsResponse.getShortName());
//
//    List<Map<String, Object>> allPosts = new ArrayList<>();
//
//    posts.forEach(
//        post -> {
//          Map<String, Object> filteredPosts = new HashMap<>();
//          filteredPosts.put("postId", post.getId());
//          filteredPosts.put("userId", post.getUserId());
//          filteredPosts.put("name", userAccountService.getUserById(post.getUserId()).getUsername());
//          filteredPosts.put("time", post.getCreated());
//          filteredPosts.put("content", post.getPostContent());
//          filteredPosts.put("comments", getCommentsForThePost(post.getId()));
//
//          allPosts.add(filteredPosts);
//        });
//    postWithComments.put("posts", allPosts);
//    return postWithComments;
//  }
//
//  /**
//   * A helper function that will retrieve all the comments for a given post and return them as a
//   * list of maps.
//   *
//   * @param postId A post ID of type Integer
//   * @return A list containing all the comments for the post as HashMap objects.
//   */
//  private List<Map<String, Object>> getCommentsForThePost(String postId) {
//    List<Map<String, Object>> comments = new ArrayList<>();
//    commentService
//        .getCommentsForGivenPost(postId)
//        .forEach(
//            comment -> {
//              Map<String, Object> commentObject = new HashMap<>();
//              commentObject.put("commentId", comment.getId());
//              commentObject.put("userId", comment.getUserId());
//              commentObject.put(
//                  "name", userAccountService.getUserById(comment.getUserId()).getUsername());
//              commentObject.put("time", comment.getCreated());
//              commentObject.put("content", comment.getCommentContent());
//              comments.add(commentObject);
//            });
//
//    return comments;
//  }
//}
