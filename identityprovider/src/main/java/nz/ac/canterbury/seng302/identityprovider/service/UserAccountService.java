package nz.ac.canterbury.seng302.identityprovider.service;

import com.google.protobuf.ByteString;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.io.Console;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import net.devh.boot.grpc.server.service.GrpcService;
import nz.ac.canterbury.seng302.identityprovider.exceptions.IrremovableRoleException;
import nz.ac.canterbury.seng302.identityprovider.exceptions.UserDoesNotExistException;
import nz.ac.canterbury.seng302.shared.identityprovider.*;
import nz.ac.canterbury.seng302.shared.util.FileUploadStatus;
import nz.ac.canterbury.seng302.shared.util.FileUploadStatusResponse;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This base service contains multiple other services, and is used as a hub so GRPC does not
 * complain about multiple services extending UserAccountServiceGrpc.UserAccountServiceImplBase.
 */
@GrpcService
public class UserAccountService extends UserAccountServiceGrpc.UserAccountServiceImplBase {
    @Autowired private RegisterServerService registerServerService;

    @Autowired private GetUserService getUserService;

    @Autowired private RoleService roleService;

    @Autowired private EditUserService editUserService;

    /**
     * This is a GRPC user service method that is being over-ridden to register a user and return
     * a UserRegisterRequest
     *
     * @param request parameters from the caller
     * @param responseObserver to receive results or errors
     */
    @Override
    public void register(
            UserRegisterRequest request, StreamObserver<UserRegisterResponse> responseObserver) {
        try {
            var response = registerServerService.register(request);

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            responseObserver.onError(e);
        }
    }

    /**
     * This is a GRPC user service method that is being over-ridden to edit the users details and return
     *    * a EditUserResponse
     *
     * @param request parameters from the caller
     * @param responseObserver to receive results or errors
     */
    @Override
    public void editUser(EditUserRequest request, StreamObserver<EditUserResponse> responseObserver) {
        try {
            var response = editUserService.editUser(request);

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            e.printStackTrace();
            responseObserver.onError(e);
        }
    }

    /**
     * This is a GRPC user service method that is being over-ridden to get the user details and encase
     * them into a User Response body. if the user is not found the User response is set to null
     *
     * @param request parameters from the caller
     * @param responseObserver to receive results or errors
     */
    @Override
    public void getUserAccountById(GetUserByIdRequest request, StreamObserver<UserResponse> responseObserver) {
        try {
            var res = getUserService.getUserAccountById(request);

            responseObserver.onNext(res);
            responseObserver.onCompleted();
        } catch (Exception e) {
            e.printStackTrace();
            responseObserver.onError(e);
        }
    }

    /**
     * GRPC service method that provides a list of user details with a caller-supplied sort order,
     * maximum length, and offset.
     *
     * @param request parameters from the caller
     * @param responseObserver to receive results or errors
     */
    @Override
    public void getPaginatedUsers(
            GetPaginatedUsersRequest request, StreamObserver<PaginatedUsersResponse> responseObserver) {
        try {
            var response = getUserService.getPaginatedUsers(request);

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
        catch (Exception e) {
            e.printStackTrace();
            responseObserver.onError(e);
        }
    }

    /**
     * Adds a role to the user if the user does not already have the role.
     *
     * @param modificationRequest the modification request for the role
     * @param responseObserver to receive results or errors
     */
    @Override
    public void addRoleToUser(
            ModifyRoleOfUserRequest modificationRequest,
            StreamObserver<UserRoleChangeResponse> responseObserver) {
        try {
            var response = roleService.addRoleToUser(modificationRequest);

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (UserDoesNotExistException e) {
            e.printStackTrace();
            responseObserver.onError(e);
        }
    }

    /**
     * Removes a role to the user if the user does not already have the role.
     *
     * @param modificationRequest The modification request for the role
     * @param responseObserver to receive results or errors
     */
    @Override
    public void removeRoleFromUser(
            ModifyRoleOfUserRequest modificationRequest,
            StreamObserver<UserRoleChangeResponse> responseObserver) {
        try {
            var response = roleService.removeRoleFromUser(modificationRequest);

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (UserDoesNotExistException | IrremovableRoleException e) {
            e.printStackTrace();
            responseObserver.onError(e);
        }
    }


    /**
     * Uploads the stream of data from User and sends back the status response based on the upload status.
     *
     * @param responseObserver is of FileUploadStatusResponse
     * @return a UploadUserProfilePhotoRequest
     */
    @Override
    public StreamObserver<UploadUserProfilePhotoRequest> uploadUserProfilePhoto(StreamObserver<FileUploadStatusResponse> responseObserver) {
        return new StreamObserver<UploadUserProfilePhotoRequest>() {
            @Override
            public void onNext(UploadUserProfilePhotoRequest request) {
                int userId = request.getMetaData().getUserId();
                ByteString rawImage = request.getFileContent();

                FileUploadStatusResponse.Builder reply = FileUploadStatusResponse.newBuilder();
                FileUploadStatusResponse uploadStatus = editUserService.UploadUserPhoto(userId, rawImage);

                if (uploadStatus.getStatus() == FileUploadStatus.FAILED) {
                    responseObserver.onError(new Throwable(String.valueOf(uploadStatus.getStatus())));
                    return;
                }

                reply.setStatus(uploadStatus.getStatus())
                        .setMessage(uploadStatus.getMessage());

                responseObserver.onNext(reply.build());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println(t.getMessage());
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }


}
