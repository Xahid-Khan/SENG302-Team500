package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.authentication.PortfolioPrincipal;
import nz.ac.canterbury.seng302.portfolio.model.contract.basecontract.BasePostContract;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateService;
import nz.ac.canterbury.seng302.portfolio.service.GroupsClientService;
import nz.ac.canterbury.seng302.portfolio.service.PostService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountService;
import nz.ac.canterbury.seng302.shared.identityprovider.GroupDetailsResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


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
        int userId = getUserId(principal);
        postService.createPost(newPost, userId);
        return ResponseEntity.ok().build();
    }
}
