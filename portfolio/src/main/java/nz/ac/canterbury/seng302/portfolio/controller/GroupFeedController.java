package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.authentication.PortfolioPrincipal;
import nz.ac.canterbury.seng302.portfolio.model.contract.basecontract.BaseCommentContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.basecontract.BasePostContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.PostModel;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.shared.identityprovider.GroupDetailsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;


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


    public GroupFeedController(AuthStateService authStateService, UserAccountService userAccountService) {
        super(authStateService, userAccountService);
    }


    @GetMapping(value = "/group_feed/{groupId}", produces = "application/json")
    public String getGroupFeed(@PathVariable Integer groupId) {
        return "group_feed";
    }

    @GetMapping(value = "/feed_content/{groupId}", produces = "application/json")
    public ResponseEntity<?> getFeedContent(@PathVariable Integer groupId) {
        getDummyTestData(); //This function would be needed to delete later
        try {
            GroupDetailsResponse groupDetailsResponse = groupsClientService.getGroupById(groupId);
            List<PostModel> allPosts = postService.getAllPostsForAGroup(groupDetailsResponse.getGroupId());
            Map<String, Object> data = combineAndPrepareForFrontEnd(allPosts, groupDetailsResponse);
            return ResponseEntity.ok(data);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping(value = "/group_feed/new_post", produces = "application/json")
    public ResponseEntity<?> addNewPost(@AuthenticationPrincipal PortfolioPrincipal principal, @RequestBody BasePostContract newPost){
        try {
            int userId = getUserId(principal);
            postService.createPost(newPost, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping(value = "/delete_feed/{postId}", produces = "application/json")
    public ResponseEntity<?> deletePost(@AuthenticationPrincipal PortfolioPrincipal principal, @PathVariable int postId) {
        try {
            int userId = getUserId(principal);
            PostModel post = postService.getPostById(postId);
            if (userId == post.getUserId() || isTeacher(principal)) {
                postService.deletePost(postId);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping(value = "/update_feed/{postId}", produces = "application/json")
    public ResponseEntity<?> updatePost(@AuthenticationPrincipal PortfolioPrincipal principal,
                                     @PathVariable int postId,
                                     @RequestBody BasePostContract updatedPost) {
        try{
            int userId = getUserId(principal);
            PostModel post = postService.getPostById(postId);
            if (userId == post.getUserId() || isTeacher(principal)) {
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

    private Map<String, Object> combineAndPrepareForFrontEnd(List<PostModel> posts, GroupDetailsResponse groupDetailsResponse) {
        Map<String, Object> postWithComments = new HashMap<>();
        postWithComments.put("groupId", groupDetailsResponse.getGroupId());
        postWithComments.put("shortName", groupDetailsResponse.getShortName());

        List<Map<String, Object>> allPosts = new ArrayList<>();

        posts.forEach(post -> {
            Map<String, Object> filteredPosts = new HashMap<>();
            filteredPosts.put("postId", post.getId());
            filteredPosts.put("name", userAccountService.getUserById(post.getUserId()).getUsername());
            filteredPosts.put("time", post.getCreated());
            filteredPosts.put("content", post.getPostContent());
            filteredPosts.put("comments", getCommentsForThePost(post.getId()));

            allPosts.add(filteredPosts);
        });
        postWithComments.put("posts", allPosts);
        return postWithComments;
    }

    private List<Map<String, Object>> getCommentsForThePost(int postId) {
        List<Map<String, Object>> comments = new ArrayList<>();
        commentService.getCommentsForGivenPost(postId).forEach(comment -> {
            Map<String, Object> commentObject = new HashMap<>();
            commentObject.put("commentId", comment.getId());
            commentObject.put("name", userAccountService.getUserById(comment.getUserId()).getUsername());
            commentObject.put("time", comment.getCreated());
            commentObject.put("content", comment.getCommentContent());
            comments.add(commentObject);
        });

        return comments;
    }


    public void getDummyTestData() {
        postService.createPost(new BasePostContract(1, "This is a test Posts"), 3);
        List<PostModel> postModel = postService.getAllPostsForAGroup(1);
        commentService.addNewCommentsToAPost(new BaseCommentContract(3, postModel.get(0).getId(), "This a comment to a post"));
    }

}
