package nz.ac.canterbury.seng302.portfolio.controller;

import io.grpc.StatusRuntimeException;
import nz.ac.canterbury.seng302.portfolio.DTO.EditedUserValidation;
import nz.ac.canterbury.seng302.portfolio.DTO.User;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateService;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
import nz.ac.canterbury.seng302.portfolio.service.UploadPhotoService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountService;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class EditAccountController {

  @InitBinder
  public void initBinder(WebDataBinder binder) {
    binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
  }

    @Autowired
    private UploadPhotoService uploadPhotoService;

    @Autowired
    private RegisterClientService registerClientService;

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private AuthStateService authStateService;

  @GetMapping(value = "/edit_account")
  public String getPage(Model model, @AuthenticationPrincipal AuthState principal) {

    Integer userId = authStateService.getId(principal);

    UserResponse userDetails = userAccountService.getUserById(userId);

    // Prefill the form with the user's details
    model.addAttribute("userId", userId);
    model.addAttribute("username", userDetails.getUsername());
    model.addAttribute("user", userDetails);

    return "edit_account";
  }

  /**
   * This controller receives a profile photo (file) and crops it to 1:1 and compress it to make
   * sure it's lower than 5mb. Then it used the gRPC protocols provided in registerClientService to
   * save the file in the DataBase in bytes (ByteString) format.
   *
   * @param user A User of ty User.
   * @param bindingResult An interface that extends errors
   * @param model HTML model DTO
   * @param principal An Authority State to verify user.
   * @param file Image that user wants to save.
   * @return
   */
  @PostMapping(value = "/edit_account")
  public String postPage(
      @ModelAttribute @Validated(EditedUserValidation.class) User user,
      BindingResult bindingResult,
      Model model,
      @AuthenticationPrincipal AuthState principal,
      @RequestParam(value = "image", required = false) MultipartFile file) {

    if (bindingResult.hasErrors()) {
      return "edit_account";
    }
    model.addAttribute("user", user);
    try {
      Integer userId = authStateService.getId(principal);
      model.addAttribute("userId", userId);
      if (file != null && !file.isEmpty()) {
        if (file.getSize() > (5 * 1024) && file.getSize() < 5242000) {
          byte[] uploadImage = uploadPhotoService.imageProcessing(file);
          String fileType = uploadPhotoService.getFileType();

          registerClientService.uploadUserPhoto(userId, fileType, uploadImage);

        } else {
          model.addAttribute("imageError", "File size must be more than 5KB and less than 5MB.");
          return "edit_account";
        }
      }

      registerClientService.updateDetails(user, userId);

        } catch (StatusRuntimeException e) {
        model.addAttribute("error", "Error connecting to Identity Provider...");
        return "edit_account";
    }
      return "redirect:my_account?edited=details";
  }

    /**
     * A controller (endpoint) for deleting a user photo.
     * @param principal An Authority State to verify user.
     * @param user A user of type User.
     * @param model HTML model DTO
     * @return a String to redirect the page to.
     */
    @PostMapping(value = "/edit_account/imageDelete")
    public String deleteUserPhoto(@AuthenticationPrincipal AuthState principal, @ModelAttribute User user, Model model) {

        int userId = authStateService.getId(principal);
        registerClientService.deleteUserPhoto(userId);
        model.addAttribute("user", user);
        return "redirect:/my_account";
    }

}
