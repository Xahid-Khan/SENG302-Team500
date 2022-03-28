package nz.ac.canterbury.seng302.identityprovider.service;

import io.grpc.stub.StreamObserver;
import nz.ac.canterbury.seng302.identityprovider.database.UserModel;
import nz.ac.canterbury.seng302.identityprovider.database.UserRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.EditUserRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.EditUserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
public class EditUserServerServiceTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository repository;

    @Autowired
    PasswordService passwordService;

    @Autowired
    RegisterServerService editUserServerService;

    private UserModel newUser;
    private EditUserRequest.Builder modifiedUser;

    @BeforeEach
    void setup() {

        try {
            String password = passwordService.hashPassword("Apassword");
            newUser = new UserModel("JamesSmith",
                    password,
                    "James",
                    "Smith",
                    "Smith",
                    "Jamie",
                    "This is a dummy profile",
                    "His/Him",
                    "jamessmith@gmail.com");
            newUser.setId(1);
            repository.save(newUser);
            modifiedUser = EditUserRequest.newBuilder();
            modifiedUser.setUserId(newUser.getId());
            modifiedUser.setFirstName(newUser.getFirstName());
            modifiedUser.setLastName(newUser.getLastName());
            modifiedUser.setMiddleName(newUser.getMiddleName());
            modifiedUser.setNickname(newUser.getNickname());
            modifiedUser.setEmail(newUser.getEmail());
            modifiedUser.setPersonalPronouns(newUser.getPronouns());
            modifiedUser.setBio(newUser.getBio());
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    @Test
    public void UpdateValidFirstName() {
        modifiedUser.setFirstName("Joy");
        StreamObserver<EditUserResponse> userResponse = new StreamObserver<EditUserResponse>() {
            @Override
            public void onNext(EditUserResponse value) {
                assertEquals(true, value.getIsSuccess());
            }

            @Override
            public void onError(Throwable t) {}

            @Override
            public void onCompleted() {}
        };
        editUserServerService.editUser(modifiedUser.build(), userResponse);
    }


    @Test
    public void UpdateInvalidFirstName() {
        modifiedUser.setFirstName("");
        StreamObserver<EditUserResponse> userResponse = new StreamObserver<EditUserResponse>() {
            @Override
            public void onNext(EditUserResponse value) {
                assertEquals(false, value.getIsSuccess());
                assertEquals("Invalid First Name", value.getMessage());
            }

            @Override
            public void onError(Throwable t) {}

            @Override
            public void onCompleted() {}
        };
        editUserServerService.editUser(modifiedUser.build(), userResponse);
        modifiedUser.setFirstName("123");
        editUserServerService.editUser(modifiedUser.build(), userResponse);
        modifiedUser.setFirstName("Jame@DSD");
        editUserServerService.editUser(modifiedUser.build(), userResponse);
    }


    @Test
    public void UpdateValidLastName() {
        modifiedUser.setLastName("Joy");
        StreamObserver<EditUserResponse> userResponse = new StreamObserver<EditUserResponse>() {
            @Override
            public void onNext(EditUserResponse value) {
                assertEquals(true, value.getIsSuccess());
            }

            @Override
            public void onError(Throwable t) {}

            @Override
            public void onCompleted() {}
        };
        editUserServerService.editUser(modifiedUser.build(), userResponse);
    }


    @Test
    public void UpdateInvalidLastName() {
        modifiedUser.setLastName("");
        StreamObserver<EditUserResponse> userResponse = new StreamObserver<EditUserResponse>() {
            @Override
            public void onNext(EditUserResponse value) {
                assertEquals(false, value.getIsSuccess());
                assertEquals("Invalid Last Name", value.getMessage());
            }

            @Override
            public void onError(Throwable t) {}

            @Override
            public void onCompleted() {}
        };
        editUserServerService.editUser(modifiedUser.build(), userResponse);
        modifiedUser.setLastName("123");
        editUserServerService.editUser(modifiedUser.build(), userResponse);
        modifiedUser.setLastName("Jame@DSD");
        editUserServerService.editUser(modifiedUser.build(), userResponse);
    }



}
