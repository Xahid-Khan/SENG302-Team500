package nz.ac.canterbury.seng302.identityprovider.service;

import com.google.protobuf.ByteString;
import nz.ac.canterbury.seng302.identityprovider.database.PhotoModel;
import nz.ac.canterbury.seng302.identityprovider.database.UserModel;
import nz.ac.canterbury.seng302.identityprovider.database.UserPhotoRepository;
import nz.ac.canterbury.seng302.identityprovider.database.UserRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.EditUserRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.EditUserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UploadUserProfilePhotoRequest;
import nz.ac.canterbury.seng302.shared.util.FileUploadStatus;
import nz.ac.canterbury.seng302.shared.util.FileUploadStatusResponse;
import nz.ac.canterbury.seng302.shared.util.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EditUserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private UserPhotoRepository photoRepository;

    public EditUserResponse editUser(EditUserRequest request) {
        EditUserResponse.Builder reply = EditUserResponse.newBuilder();
        UserModel existingUser = repository.findById(request.getUserId());
        if (existingUser == null) {
            reply
                    .setIsSuccess(false)
                    .setMessage("Error: User not in database")
                    .addValidationErrors(
                            ValidationError.newBuilder()
                                    .setFieldName("ID")
                                    .setErrorText("Error: User not in database"));
        } else {
            UserModel newUser =
                    new UserModel(
                            existingUser.getUsername(),
                            existingUser.getPasswordHash(),
                            request.getFirstName(),
                            request.getMiddleName(),
                            request.getLastName(),
                            request.getNickname(),
                            request.getBio(),
                            request.getPersonalPronouns(),
                            request.getEmail(),
                            existingUser.getRoles(),
                            existingUser.getCreated());
            newUser.setId(request.getUserId());
            repository.save(newUser);
            reply.setIsSuccess(true).setMessage("Updated details for user: " + newUser);
        }

        return reply.build();
    }


    /**
     * This method saves the raw image data into the database in ByteString format.
     * @param userId Id (integer) of the user
     * @param rawImageData Image that needs to be saved to DB
     * @return a FileUploadStatusResponse
     */
    public FileUploadStatusResponse UploadUserPhoto(int userId, byte[] rawImageData) {
        FileUploadStatusResponse.Builder reply = FileUploadStatusResponse.newBuilder();
        try {
            PhotoModel newPhoto =
                    new PhotoModel(
                            userId,
                            rawImageData);
            photoRepository.save(newPhoto);
            reply.setStatus(FileUploadStatus.SUCCESS);
        } catch (Exception e) {
            reply
                    .setStatus(FileUploadStatus.FAILED)
                    .setMessage(e.getMessage());
        }
        return reply.build();
    }
}
