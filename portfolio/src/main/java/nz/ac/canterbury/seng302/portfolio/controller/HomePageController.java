package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.authentication.PortfolioPrincipal;
import nz.ac.canterbury.seng302.portfolio.model.contract.SubscriptionContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.PostModel;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.shared.identityprovider.GroupDetailsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Handles the post and delete requests on the /subscribe endpoint. */
@Controller
@RequestMapping("/api/v1")
public class HomePageController {

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private AuthStateService authStateService;

    @Autowired
    private PostService postService;

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private ReactionService reactionService;

    @Autowired
    private CommentService commentService;

    /** Handles post requests on the /subscribe endpoint to subscribe a user to a group. */
    @PostMapping(value = "/subscribe", produces = "application/json")
    public ResponseEntity<?> subscribe(@AuthenticationPrincipal PortfolioPrincipal principal,
                                       @RequestBody SubscriptionContract subscription) {
        try{
            subscriptionService.subscribe(subscription);
        } catch (HttpMessageNotReadableException e){
            return ResponseEntity.badRequest().build();
        } catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().build();
    }

    /** Handles delete requests on the /subscribe endpoint to unsubscribe a user from a group. */
    @DeleteMapping(value = "/subscribe", produces = "application/json")
    public ResponseEntity<?> unsubscribe(@AuthenticationPrincipal PortfolioPrincipal principal,
                                         @RequestBody SubscriptionContract subscription) {
        try{
            subscriptionService.unsubscribe(subscription);
        } catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/subscribe/{userId}", produces = "application/json")
    public ResponseEntity<?> getAll(@AuthenticationPrincipal PortfolioPrincipal principal,
                                         @PathVariable int userId) {
        try{
            var subscriptions = subscriptionService.getAll(userId);
            return ResponseEntity.ok(subscriptions);
        } catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping(value = "/posts", produces = "application/json")
    public ResponseEntity<?> getAllPosts(@AuthenticationPrincipal PortfolioPrincipal principal) {
        try {
            List<PostModel> posts = postService.getAllPosts();
            Map<String, Object> data = combineAndPrepareForFrontEnd(posts);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * This function creates a Map from posts to send it to the front end as JSON object.
     *
     * @param posts                All the posts from a group as a List
     * @return A Hash Map where first element is string and second is an object.
     */
    private Map<String, Object> combineAndPrepareForFrontEnd(List<PostModel> posts) {
        Map<String, Object> postMap = new HashMap<>();

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
            filteredPosts.put("groupId", post.getGroupId());
            filteredPosts.put("comments", commentService.getCommentsForThePostAsJson(post.getId()));

            allPosts.add(filteredPosts);
        });
        postMap.put("posts", allPosts);
        return postMap;
    }
}
