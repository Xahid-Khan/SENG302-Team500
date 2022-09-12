package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.authentication.PortfolioPrincipal;
import nz.ac.canterbury.seng302.portfolio.model.contract.CommentContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.PostContract;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateService;
import nz.ac.canterbury.seng302.portfolio.service.CommentService;
import nz.ac.canterbury.seng302.portfolio.service.GroupsClientService;
import nz.ac.canterbury.seng302.portfolio.service.PostService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class GroupFeedPageController extends AuthenticatedController {

  @Value("${nz.ac.canterbury.seng302.portfolio.urlPathPrefix}")
  private String urlPathPrefix;

  @Autowired
  private GroupsClientService groupsClientService;

  @Autowired
  private PostService postService;
  @Autowired
  private CommentService commentService;

  /**
   * This is similar to autowiring, but apparently recommended more than field injection.
   *
   * @param authStateService   an AuthStateService
   * @param userAccountService a UserAccountService
   */
  protected GroupFeedPageController(
      AuthStateService authStateService,
      UserAccountService userAccountService) {
    super(authStateService, userAccountService);
  }

  @GetMapping(value = "/group_feed/{groupId}", produces = "application/json")
  public String getGroupFeed(@PathVariable Integer groupId, Model model,
      @AuthenticationPrincipal PortfolioPrincipal principal) {
    model.addAttribute("isMember",
        groupsClientService.isMemberOfTheGroup(getUserId(principal), groupId));
    model.addAttribute("relativePath", urlPathPrefix);
    addMockDataForTesting();
    return "group_feed";
  }

  private void addMockDataForTesting() {
    if (postService.getAllPosts().size() == 0) {
      postService.createPost(new PostContract(1, "This is a test 1 post"), 3);
      postService.createPost(new PostContract(1, "This is a test 2 post"), 3);
      postService.createPost(new PostContract(1, "This is a test 3 post"), 3);
      commentService.addNewCommentsToPost(
          new CommentContract(3, postService.getAllPosts().get(0).getId(),
              "This is a comment to the post for test1."));
      commentService.addNewCommentsToPost(
          new CommentContract(3, postService.getAllPosts().get(1).getId(),
              "This is a comment to the post for test2."));
      commentService.addNewCommentsToPost(
          new CommentContract(3, postService.getAllPosts().get(0).getId(),
              "This is a comment to the post for test3."));
    }
  }

}
