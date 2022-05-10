package nz.ac.canterbury.seng302.identityprovider.controller;

import nz.ac.canterbury.seng302.identityprovider.database.PhotoModel;
import nz.ac.canterbury.seng302.identityprovider.database.UserPhotoRepository;
import nz.ac.canterbury.seng302.identityprovider.service.EditUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Controller
public class UserImageController {
    private UserPhotoRepository photoRepository;

    /**
     * This end-point will check if the user photo exist and return it.
//     * @param userId
     * @param response
     * @param userPhoto
     * @throws ServletException
     * @throws IOException
     */
    @GetMapping(value = "/userImage/")
    void showUserImage(HttpServletResponse response,
                       Optional<PhotoModel> userPhoto) throws ServletException, IOException {
        System.out.println("11111111111111111111111111");
        userPhoto = photoRepository.findById(1);
        response.setContentType("image/*");
        response.getOutputStream().write(userPhoto.get().getUserPhoto());
        response.getOutputStream().close();
    }

}
