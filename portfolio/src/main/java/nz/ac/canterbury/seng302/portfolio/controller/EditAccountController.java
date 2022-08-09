package nz.ac.canterbury.seng302.portfolio.controller;

import io.grpc.StatusRuntimeException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import nz.ac.canterbury.seng302.portfolio.DTO.EditedUserValidation;
import nz.ac.canterbury.seng302.portfolio.DTO.User;
import nz.ac.canterbury.seng302.portfolio.authentication.PortfolioPrincipal;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateService;
import nz.ac.canterbury.seng302.portfolio.service.PhotoCropService;
import nz.ac.canterbury.seng302.portfolio.service.RegisterClientService;
import nz.ac.canterbury.seng302.portfolio.service.UserAccountService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

@Controller
public class EditAccountController {

    public static final int MIN_PROFILE_PICTURE_SIZE = 5 * 1024;
    public static final int PROFILE_PICTURE_COMPRESSION_THRESHOLD = 5 * 1024 * 1024;
    public static final int MAX_PROFILE_PICTURE_SIZE = 10 * 1024 * 1024;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
    }

    @Autowired
    private PhotoCropService photoCropService;

    @Autowired
    private RegisterClientService registerClientService;

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private AuthStateService authStateService;

    @GetMapping(value = "/edit_account")
    public String getPage(Model model, @AuthenticationPrincipal PortfolioPrincipal principal) {

        Integer userId = authStateService.getId(principal);

        UserResponse userDetails = userAccountService.getUserById(userId);

        // Prefill the form with the user's details
        model.addAttribute("userId", userId);
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("user", userDetails);

        return "edit_account";
    }

    /**
     * Apply the cropping algorithm to the uploaded image and return the cropped image for previewing
     * to the user.
     *
     * @param file to generate preview for
     * @return cropped preview of the given image file
     */
    @PostMapping("/edit_account/preview_picture")
    public ResponseEntity<byte[]> generateCroppedPreview(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value = "image") MultipartFile file
    ) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("No image found.".getBytes(StandardCharsets.UTF_8))
                    ;
        }

        if (MIN_PROFILE_PICTURE_SIZE > file.getSize() || file.getSize() > MAX_PROFILE_PICTURE_SIZE) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            String.format(
                                    "Image is too small or too large. "
                                            + "Please provide an image of size between %d and %d bytes.",
                                    MIN_PROFILE_PICTURE_SIZE,
                                    MAX_PROFILE_PICTURE_SIZE
                            ).getBytes(StandardCharsets.UTF_8)
                    );
        }

        try {
            byte[] uploadImage = photoCropService.processImageFile(
                    file,
                    file.getSize() > PROFILE_PICTURE_COMPRESSION_THRESHOLD
            );
            return ResponseEntity.ok(uploadImage);
        } catch (UnsupportedMediaTypeStatusException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("This file format is not supported.".getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Failed to read/write image.".getBytes(StandardCharsets.UTF_8));
        }
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
     * @return
     */
    @PostMapping(value = "/edit_account")
    public String postPage(
            @ModelAttribute @Validated(EditedUserValidation.class) User user,
            BindingResult bindingResult,
            Model model,
            @AuthenticationPrincipal PortfolioPrincipal principal) {

        model.addAttribute("user", user);
        // Rely on the authstate to get the user, since otherwise the submitted user is used leading
        //  to the username being blank.
        Integer userId = authStateService.getId(principal);
        UserResponse userDetails = userAccountService.getUserById(userId);

        model.addAttribute("username", userDetails.getUsername());
        if (bindingResult.hasErrors()) {
            return "edit_account";
        }
        try {

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
    public String deleteUserPhoto(@AuthenticationPrincipal PortfolioPrincipal principal, @ModelAttribute User user, Model model) {

        int userId = authStateService.getId(principal);
        registerClientService.deleteUserPhoto(userId);
        model.addAttribute("user", user);
        return "redirect:/my_account";
    }

    @GetMapping(value="/edit_user_image")
    public String getPage(@AuthenticationPrincipal PortfolioPrincipal principal, Model model) {
        Integer userId = authStateService.getId(principal);

        model.addAttribute("userId", userId);

        return "edit_user_image";
    }

    @PostMapping (value = "/edit_account/editImage")
    public String changeUserProfilePhoto(@AuthenticationPrincipal PortfolioPrincipal principal,
                                         @RequestParam(value = "croppedUserImage") String croppedImage,
                                         Model model,
                                         @RequestParam(value = "image", required = false) MultipartFile file
    ) {
        try {
            if (croppedImage.length() != 0) {
                int userId = authStateService.getId(principal);
                String fileType = croppedImage.substring(5, 14);
                byte[] imageData = Base64.getDecoder().decode(croppedImage.substring(22, croppedImage.length()));
                registerClientService.uploadUserPhoto(userId, fileType, imageData);
            } else {
                try {
                    Integer userId = authStateService.getId(principal);
                    model.addAttribute("userId", userId);
                    if (file != null && !file.isEmpty()) {
                        if (
                                MIN_PROFILE_PICTURE_SIZE <= file.getSize()
                                        && file.getSize() <= MAX_PROFILE_PICTURE_SIZE
                        ) {
                            try {
                                byte[] uploadImage = photoCropService.processImageFile(
                                        file,
                                        file.getSize() > PROFILE_PICTURE_COMPRESSION_THRESHOLD
                                );

                                registerClientService.uploadUserPhoto(userId, file.getContentType(), uploadImage);
                            }
                            catch (IOException e) {
                                model.addAttribute("imageError", "Failed to save image. Please try again later or with a different image.");
                                return "edit_user_image";
                            }
                            catch (UnsupportedMediaTypeStatusException e) {
                                model.addAttribute("imageError", "Failed to save image. Please use a different image format.");
                                return "edit_user_image";
                            }

                        } else {
                            model.addAttribute("imageError", "File size must be more than 5KB and less than 5MB.");
                            return "edit_user_image";
                        }
                    }
                } catch (StatusRuntimeException e) {
                    model.addAttribute("error", "Error connecting to Identity Provider...");
                    return "edit_user_image";
                }
            }
        } catch (StatusRuntimeException e) {

        }
        return "redirect:/my_account";
    }

}
