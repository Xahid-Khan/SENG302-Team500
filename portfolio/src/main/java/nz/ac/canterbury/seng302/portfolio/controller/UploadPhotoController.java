package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.service.AuthStateService;
import nz.ac.canterbury.seng302.portfolio.service.UploadPhotoService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class UploadPhotoController {

    @Autowired
    private AuthStateService authStateService;

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private UploadPhotoService uploadPhotoService;

    @PostMapping("/uploadphoto")
    public String index(Model model, @RequestParam("image") MultipartFile file, @AuthenticationPrincipal AuthState principal) throws IOException {
        int userId = authStateService.getId(principal);
        UserResponse userDetails = userAccountService.getUserById(userId);
        if (file.getSize() > 200000 && file.getSize() < 5000000) {
            uploadPhotoService.imageProccessing(file, userDetails.getUsername() + userId);
        } else {
            model.addAttribute("imageError", "File size must be more than 500KB and less than 5MB.");
        }

        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("user", userDetails);

        return "edit_account";
    }
}
