package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.authentication.PortfolioPrincipal;
import nz.ac.canterbury.seng302.portfolio.model.contract.basecontract.BasePostContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.PostModel;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateService;
import nz.ac.canterbury.seng302.portfolio.service.GroupsClientService;
import nz.ac.canterbury.seng302.portfolio.service.PostService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountService;
import nz.ac.canterbury.seng302.shared.identityprovider.GroupDetailsResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
public class GroupFeedController extends AuthenticatedController {

    private PostService postService;

    private GroupsClientService groupsClientService;


    public GroupFeedController(AuthStateService authStateService, UserAccountService userAccountService) {
        super(authStateService, userAccountService);
    }


    @GetMapping(value = "/group_feed/{groupId}", produces = "application/json")
    public String getGroupFeed(@PathVariable Integer groupId) {

        GroupDetailsResponse groupDetailsResponse = groupsClientService.getGroupById(groupId);
        return "group_feed";
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



}
