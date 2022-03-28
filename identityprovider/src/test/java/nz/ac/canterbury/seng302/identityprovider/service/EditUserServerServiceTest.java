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

import java.util.stream.Stream;

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
            System.out.println("cccccccccccccccccccccccc");
            repository.save(newUser);
            System.out.println("22222222222222222222222222222");
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
            System.out.println("000000000000000000000000");
            e.printStackTrace();
        }
    }

    @Test
    public void UpdateValidFirstName() {
        System.out.println(modifiedUser.toString());
        modifiedUser.setFirstName("Joy");
        System.out.println(modifiedUser.toString());
        StreamObserver<EditUserResponse> userResponse = new StreamObserver<EditUserResponse>() {
            @Override
            public void onNext(EditUserResponse value) {
                assertEquals(value.getIsSuccess(), true);
            }

            @Override
            public void onError(Throwable t) {}

            @Override
            public void onCompleted() {}
        };
        System.out.println(modifiedUser.toString());
        editUserServerService.editUser(modifiedUser.build(), userResponse);
        System.out.println("111111111111111111111111111111111111111111111111");

    }


}
