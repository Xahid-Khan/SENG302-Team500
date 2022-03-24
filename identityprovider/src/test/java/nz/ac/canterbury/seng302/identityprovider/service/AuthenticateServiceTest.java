package nz.ac.canterbury.seng302.identityprovider.service;

import io.grpc.stub.StreamObserver;
import nz.ac.canterbury.seng302.identityprovider.database.UserModel;
import nz.ac.canterbury.seng302.identityprovider.database.UserRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticateRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthenticateResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthenticateServiceTest {

    @Autowired
    AuthenticateServerService service;

    @Autowired
    UserRepository repository;

    @Autowired
    PasswordService passwordService;

    @BeforeAll
    void createUsers() {
        try {
            UserModel user1 = new UserModel("a", passwordService.hashPassword("password"), "a", "", "a", "", "", "", "a@a");
            UserModel user2 = new UserModel("b", passwordService.hashPassword("password"), "b", "", "b", "", "", "", "b@b");
            repository.save(user1);
            repository.save(user2);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    void testPasswordCorrect() throws NoSuchAlgorithmException, InvalidKeySpecException {
        AuthenticateRequest req = AuthenticateRequest.newBuilder().setUsername("a").setPassword("password").build();
        StreamObserver<AuthenticateResponse> so = new StreamObserver<AuthenticateResponse>() {
            @Override
            public void onNext(AuthenticateResponse value) {
                assertTrue(value.getSuccess());
            }
            @Override
            public void onError(Throwable t) {}
            @Override
            public void onCompleted() {}
        };
        service.authenticate(req, so);
    }

    @Test
    void testPasswordIncorrect() throws NoSuchAlgorithmException, InvalidKeySpecException {
        AuthenticateRequest req = AuthenticateRequest.newBuilder().setUsername("a").setPassword("wrong").build();
        StreamObserver<AuthenticateResponse> so = new StreamObserver<AuthenticateResponse>() {
            @Override
            public void onNext(AuthenticateResponse value) {
                assertFalse(value.getSuccess());
            }
            @Override
            public void onError(Throwable t) {}
            @Override
            public void onCompleted() {}
        };
        service.authenticate(req, so);
    }

    @Test
    void testUsernameIncorrect() throws NoSuchAlgorithmException, InvalidKeySpecException {
        AuthenticateRequest req = AuthenticateRequest.newBuilder().setUsername("wrong").setPassword("password").build();
        StreamObserver<AuthenticateResponse> so = new StreamObserver<AuthenticateResponse>() {
            @Override
            public void onNext(AuthenticateResponse value) {
                assertFalse(value.getSuccess());
            }
            @Override
            public void onError(Throwable t) {}
            @Override
            public void onCompleted() {}
        };
        service.authenticate(req, so);
    }

}
