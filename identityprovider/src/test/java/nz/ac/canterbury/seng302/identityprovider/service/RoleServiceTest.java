package nz.ac.canterbury.seng302.identityprovider.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import io.grpc.stub.StreamObserver;
import java.util.List;
import nz.ac.canterbury.seng302.identityprovider.database.UserModel;
import nz.ac.canterbury.seng302.shared.identityprovider.ModifyRoleOfUserRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRoleChangeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RoleServiceTest {
  @Autowired private RoleService roleService;

  private StreamObserver<UserRoleChangeResponse> observer = mock(StreamObserver.class);

  @BeforeEach
  void addUser() {
    roleService.getRepository().save(
        new UserModel(
            "Username",
            "hash",
            "FirstName",
            "MiddleName",
            "LastName",
            "NickName",
            "Bio",
            "Pronouns",
            "Email",
            List.of(UserRole.STUDENT)));
  }

  /** Adds a teacher role to a user, then a course administrator role to a user. */
  @Test
  void addRolesToUser() {
    roleService.addRoleToUser(
        ModifyRoleOfUserRequest.newBuilder().setUserId(1).setRole(UserRole.TEACHER).build(),
        observer);

    Mockito.verify(observer, times(1)).onCompleted();

    ArgumentCaptor<UserRoleChangeResponse> captor =
        ArgumentCaptor.forClass(UserRoleChangeResponse.class);

    Mockito.verify(observer, times(1)).onNext(captor.capture());

    UserRoleChangeResponse response = captor.getValue();

    assertTrue(response.getIsSuccess());

    assertEquals(2, roleService.getRepository().findById(1).getRoles().size());
  }
}
