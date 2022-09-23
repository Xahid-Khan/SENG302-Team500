package nz.ac.canterbury.seng302.portfolio.controller.feed;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeFeedPageController {
  @GetMapping(value = "/home_feed", produces = "application/json")
  public String getHomeFeed() {
    return "home_page";
  }
}
