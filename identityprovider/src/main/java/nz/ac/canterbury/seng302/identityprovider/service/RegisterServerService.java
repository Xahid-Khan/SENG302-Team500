package nz.ac.canterbury.seng302.identityprovider.service;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import nz.ac.canterbury.seng302.identityprovider.database.UserModel;
import nz.ac.canterbury.seng302.identityprovider.database.UserRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegisterServerService {

  @Autowired private UserRepository repository;

  @Autowired private PasswordService passwordService;

  public UserRegisterResponse register(UserRegisterRequest request)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    UserRegisterResponse.Builder reply = UserRegisterResponse.newBuilder();

    var passwordHash = passwordService.hashPassword(request.getPassword());
    List<UserRole> roles = new ArrayList<>();
    roles.add(UserRole.STUDENT);
    UserModel user =
        new UserModel(
            request.getUsername(),
            passwordHash,
            request.getFirstName(),
            request.getMiddleName(),
            request.getLastName(),
            request.getNickname(),
            request.getBio(),
            request.getPersonalPronouns(),
            request.getEmail(),
            roles);
    repository.save(user);
    reply.setIsSuccess(true).setNewUserId(user.getId()).setMessage("registered new user: " + user);

    return reply.build();
  }
}
