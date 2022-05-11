package nz.ac.canterbury.seng302.identityprovider.controller;

import nz.ac.canterbury.seng302.identityprovider.database.PhotoModel;
import nz.ac.canterbury.seng302.identityprovider.database.UserPhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.html.Option;
import java.io.IOException;
import java.util.Optional;

@Controller
public class UserImageController {
    @Autowired
    private UserPhotoRepository photoRepository;

    /**
     * This end-point will check if the user photo exist and return it.
     * @param userId
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @GetMapping(value = "/userImage/{id}")
    void showUserImage(@PathVariable("id") int userId, HttpServletResponse response) throws IOException {
        response.setContentType("image/*");
        try (ServletOutputStream imageStream = response.getOutputStream()) {
            PhotoModel userPhoto = photoRepository.findById(userId).orElse(null);
            if (userPhoto != null) {
                imageStream.write(userPhoto.getUserPhoto());
            }
        }
    }

}
