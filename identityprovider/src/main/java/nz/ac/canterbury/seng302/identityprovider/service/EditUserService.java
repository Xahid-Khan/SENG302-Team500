package nz.ac.canterbury.seng302.identityprovider.service;

import com.google.protobuf.ByteString;
import nz.ac.canterbury.seng302.identityprovider.database.UserModel;
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

    public EditUserResponse editUser(EditUserRequest request) {
        EditUserResponse.Builder reply = EditUserResponse.newBuilder();
        UserModel existingUser = repository.findById(request.getUserId());
        ByteString imageData = null;
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
                            existingUser.getCreated(),
                            imageData);
            newUser.setId(request.getUserId());
            repository.save(newUser);
            reply.setIsSuccess(true).setMessage("Updated details for user: " + newUser);
        }

        return reply.build();
    }


    public void UploadUserPhoto(UploadUserProfilePhotoRequest uploadImageData) {
        FileUploadStatusResponse.Builder reply = FileUploadStatusResponse.newBuilder();
        UserModel user = repository.findById(uploadImageData.getMetaData().getUserId());
        try {
            UserModel newUser =
                    new UserModel(
                            user.getUsername(),
                            user.getPasswordHash(),
                            user.getFirstName(),
                            user.getMiddleName(),
                            user.getLastName(),
                            user.getNickname(),
                            user.getBio(),
                            user.getPersonalPronouns(),
                            user.getEmail(),
                            user.getRoles(),
                            user.getCreated(),
                            uploadImageData.getFileContent());
            newUser.setId(user.getId());
            repository.save(newUser);
        } catch (Exception e) {
            reply
                    .setStatus(FileUploadStatus.FAILED)
                    .setMessage(e.getMessage());
        }
    }
}
