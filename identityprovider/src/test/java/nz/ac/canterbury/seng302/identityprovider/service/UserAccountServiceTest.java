package nz.ac.canterbury.seng302.identityprovider.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

import io.grpc.stub.StreamObserver;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import nz.ac.canterbury.seng302.identityprovider.database.UserModel;
import nz.ac.canterbury.seng302.identityprovider.database.UserRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.GetPaginatedUsersRequest;
import nz.ac.canterbury.seng302.shared.identityprovider.PaginatedUsersResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import nz.ac.canterbury.seng302.shared.util.PaginationRequestOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserAccountServiceTest {

  @Autowired private PasswordService passwordService;

  @Autowired private UserRepository repository;

  @Autowired private UserAccountService service;

  @Mock private StreamObserver<PaginatedUsersResponse> responseObserver;

  @Captor private ArgumentCaptor<PaginatedUsersResponse> onNextArgumentCaptor;

  @Captor private ArgumentCaptor<Throwable> onErrorArgumentCaptor;

  @BeforeEach
  public void setup() throws NoSuchAlgorithmException, InvalidKeySpecException {
    repository.deleteAll();

    var user1 =
        new UserModel(
            "u1",
            passwordService.hashPassword("pass"),
            "c",
            "a",
            "a",
            "Y",
            "u1bio",
            "they/them",
            "u1@example.com",
            List.of(UserRole.STUDENT, UserRole.TEACHER));
    var user2 =
        new UserModel(
            "u2",
            passwordService.hashPassword("pass"),
            "b",
            "b",
            "b",
            "Z",
            "u2bio",
            "he/him",
            "u2@example.com",
            List.of(UserRole.STUDENT, UserRole.COURSE_ADMINISTRATOR));
    var user3 =
        new UserModel(
            "u3",
            passwordService.hashPassword("pass"),
            "a",
            "c",
            "c",
            "X",
            "u3bio",
            "she/her",
            "u3@example.com",
            List.of(UserRole.TEACHER));

    repository.saveAll(List.of(user1, user2, user3));
  }

  @Test
  public void given3UsersExist_whenRequestOffset0Limit2_Receive2ResultSetSize3() {
    var request =
        GetPaginatedUsersRequest.newBuilder()
            .setPaginationRequestOptions(
                PaginationRequestOptions.newBuilder()
                    .setLimit(2)
                    .setOffset(0)
                    .setOrderBy("username|asc")
                    .build())
            .build();

    service.getPaginatedUsers(request, responseObserver);

    verify(responseObserver).onNext(onNextArgumentCaptor.capture());
    var response = onNextArgumentCaptor.getValue();

    assertEquals(3, response.getPaginationResponseOptions().getResultSetSize());
    assertEquals(2, response.getUsersList().size());
  }

  @Test
  public void given3UsersExist_whenRequestOffset0Limit5_Receive3() {
    var request =
        GetPaginatedUsersRequest.newBuilder()
            .setPaginationRequestOptions(
                PaginationRequestOptions.newBuilder()
                    .setLimit(5)
                    .setOffset(0)
                    .setOrderBy("username|asc")
                    .build())
            .build();

    service.getPaginatedUsers(request, responseObserver);

    verify(responseObserver).onNext(onNextArgumentCaptor.capture());
    var response = onNextArgumentCaptor.getValue();

    assertEquals(3, response.getPaginationResponseOptions().getResultSetSize());
    assertEquals(3, response.getUsersList().size());
  }

  @Test
  public void given3UsersExist_whenRequestOffset0Limit2OrderByUsername_ReceiveFirst2Users() {
    var request =
        GetPaginatedUsersRequest.newBuilder()
            .setPaginationRequestOptions(
                PaginationRequestOptions.newBuilder()
                    .setLimit(2)
                    .setOffset(0)
                    .setOrderBy("username|asc")
                    .build())
            .build();

    service.getPaginatedUsers(request, responseObserver);

    verify(responseObserver).onNext(onNextArgumentCaptor.capture());
    var response = onNextArgumentCaptor.getValue();

    assertArrayEquals(
        new String[] {"u1", "u2"},
        response.getUsersList().stream().map(UserResponse::getUsername).toArray());
  }

  @Test
  public void given3UsersExist_whenRequestOffset3_ReceiveNoUsersResultSetSize3() {
    var request =
        GetPaginatedUsersRequest.newBuilder()
            .setPaginationRequestOptions(
                PaginationRequestOptions.newBuilder()
                    .setLimit(5)
                    .setOffset(3)
                    .setOrderBy("username|asc")
                    .build())
            .build();

    service.getPaginatedUsers(request, responseObserver);

    verify(responseObserver).onNext(onNextArgumentCaptor.capture());
    var response = onNextArgumentCaptor.getValue();

    assertEquals(3, response.getPaginationResponseOptions().getResultSetSize());
    assertEquals(0, response.getUsersList().size());
  }

  @Test
  public void given3UsersExist_whenRequestOffset1Limit5OrderByUsername_ReceiveLast2Users() {
    var request =
        GetPaginatedUsersRequest.newBuilder()
            .setPaginationRequestOptions(
                PaginationRequestOptions.newBuilder()
                    .setLimit(5)
                    .setOffset(1)
                    .setOrderBy("username|asc")
                    .build())
            .build();

    service.getPaginatedUsers(request, responseObserver);

    verify(responseObserver).onNext(onNextArgumentCaptor.capture());
    var response = onNextArgumentCaptor.getValue();

    assertArrayEquals(
        new String[] {"u2", "u3"},
        response.getUsersList().stream().map(UserResponse::getUsername).toArray());
  }

  @Test
  public void whenRequestNegativeOffset_ReceiveIllegalArgumentException() {
    var request =
        GetPaginatedUsersRequest.newBuilder()
            .setPaginationRequestOptions(
                PaginationRequestOptions.newBuilder()
                    .setLimit(5)
                    .setOffset(-1)
                    .setOrderBy("username|asc")
                    .build())
            .build();

    service.getPaginatedUsers(request, responseObserver);

    verify(responseObserver).onError(onErrorArgumentCaptor.capture());
    var response = onErrorArgumentCaptor.getValue();

    assertTrue(response instanceof IllegalArgumentException);
  }

  @Test
  public void whenRequestZeroLimit_ReceiveIllegalArgumentException() {
    var request =
        GetPaginatedUsersRequest.newBuilder()
            .setPaginationRequestOptions(
                PaginationRequestOptions.newBuilder()
                    .setLimit(0)
                    .setOffset(1)
                    .setOrderBy("username|asc")
                    .build())
            .build();

    service.getPaginatedUsers(request, responseObserver);

    verify(responseObserver).onError(onErrorArgumentCaptor.capture());
    var response = onErrorArgumentCaptor.getValue();

    assertTrue(response instanceof IllegalArgumentException);
  }

  @Test
  public void whenRequestOrderByInvalid_ReceiveIllegalArgumentException() {
    var request =
        GetPaginatedUsersRequest.newBuilder()
            .setPaginationRequestOptions(
                PaginationRequestOptions.newBuilder()
                    .setLimit(5)
                    .setOffset(1)
                    .setOrderBy("middleName|asc")
                    .build())
            .build();

    service.getPaginatedUsers(request, responseObserver);

    verify(responseObserver).onError(onErrorArgumentCaptor.capture());
    var response = onErrorArgumentCaptor.getValue();

    assertTrue(response instanceof IllegalArgumentException);
  }

  @Test
  public void whenRequestEmpty_ReceiveIllegalArgumentException() {
    var request = GetPaginatedUsersRequest.newBuilder().build();

    service.getPaginatedUsers(request, responseObserver);

    verify(responseObserver).onError(onErrorArgumentCaptor.capture());
    var response = onErrorArgumentCaptor.getValue();

    assertTrue(response instanceof IllegalArgumentException);
  }

  @Test
  public void given3UsersExist_whenOrderByUsername_receiveInAscendingOrder() {
    var request =
        GetPaginatedUsersRequest.newBuilder()
            .setPaginationRequestOptions(
                PaginationRequestOptions.newBuilder()
                    .setLimit(5)
                    .setOffset(0)
                    .setOrderBy("username|asc")
                    .build())
            .build();

    service.getPaginatedUsers(request, responseObserver);

    verify(responseObserver).onNext(onNextArgumentCaptor.capture());
    var response = onNextArgumentCaptor.getValue();

    assertArrayEquals(
        new String[] {"u1", "u2", "u3"},
        response.getUsersList().stream().map(UserResponse::getUsername).toArray());
  }

  @Test
  public void given3UsersExist_whenOrderByName_receiveInAscendingOrder() {
    var request =
        GetPaginatedUsersRequest.newBuilder()
            .setPaginationRequestOptions(
                PaginationRequestOptions.newBuilder()
                    .setLimit(5)
                    .setOffset(0)
                    .setOrderBy("name|asc")
                    .build())
            .build();

    service.getPaginatedUsers(request, responseObserver);

    verify(responseObserver).onNext(onNextArgumentCaptor.capture());
    var response = onNextArgumentCaptor.getValue();

    assertArrayEquals(
        new String[] {"u3", "u2", "u1"},
        response.getUsersList().stream().map(UserResponse::getUsername).toArray());
  }

  @Test
  public void given3UsersExist_whenOrderByAlias_receiveInAscendingOrder() {
    var request =
        GetPaginatedUsersRequest.newBuilder()
            .setPaginationRequestOptions(
                PaginationRequestOptions.newBuilder()
                    .setLimit(5)
                    .setOffset(0)
                    .setOrderBy("nickname|asc")
                    .build())
            .build();

    service.getPaginatedUsers(request, responseObserver);

    verify(responseObserver).onNext(onNextArgumentCaptor.capture());
    var response = onNextArgumentCaptor.getValue();

    assertArrayEquals(
        new String[] {"u3", "u1", "u2"},
        response.getUsersList().stream().map(UserResponse::getUsername).toArray());
  }

  @Test
  public void given3UsersExist_whenOrderByUsernameDescending_receiveInDescendingOrder() {
    var request =
        GetPaginatedUsersRequest.newBuilder()
            .setPaginationRequestOptions(
                PaginationRequestOptions.newBuilder()
                    .setLimit(5)
                    .setOffset(0)
                    .setOrderBy("username|desc")
                    .build())
            .build();

    service.getPaginatedUsers(request, responseObserver);

    verify(responseObserver).onNext(onNextArgumentCaptor.capture());
    var response = onNextArgumentCaptor.getValue();

    assertArrayEquals(
        new String[] {"u3", "u2", "u1"},
        response.getUsersList().stream().map(UserResponse::getUsername).toArray());
  }

  @Test
  public void given3UsersExist_whenOrderByAlias_receiveInAscendingOrderAlphabetically() {
    var request =
        GetPaginatedUsersRequest.newBuilder()
            .setPaginationRequestOptions(
                PaginationRequestOptions.newBuilder()
                    .setLimit(5)
                    .setOffset(0)
                    .setOrderBy("roles|asc")
                    .build())
            .build();

    service.getPaginatedUsers(request, responseObserver);

    verify(responseObserver).onNext(onNextArgumentCaptor.capture());
    var response = onNextArgumentCaptor.getValue();

    assertArrayEquals(
        new String[] {"u2", "u1", "u3"},
        response.getUsersList().stream().map(UserResponse::getUsername).toArray());
  }
}
