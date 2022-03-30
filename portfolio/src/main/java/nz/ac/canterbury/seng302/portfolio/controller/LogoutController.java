package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.authentication.CookieUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;

@Controller
public class LogoutController {

    // TODO: This is not working, and I have no idea why. More research required.
    @GetMapping("/logout")
    public String logout(RedirectAttributes redirectAttributes, HttpServletResponse httpServletResponse) {
        CookieUtil.clear(httpServletResponse, "lens-session-token");

        redirectAttributes.addFlashAttribute("message", "Successfully logged out.");
        return "redirect:/login";
    }

}
