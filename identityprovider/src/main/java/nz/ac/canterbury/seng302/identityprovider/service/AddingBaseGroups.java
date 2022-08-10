package nz.ac.canterbury.seng302.identityprovider.service;

import com.google.protobuf.Timestamp;
import nz.ac.canterbury.seng302.identityprovider.database.*;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class AddingBaseGroups {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;


    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private PasswordService passwordService;

    private GroupModel teachers;
    private GroupModel nonGroup;

    /**
     * This method is listing to the application ready event, after application is ready it creates 2 student users and
     * 1 teacher user, then creates 1 teaching staff group (group of users who are teacher) and 1 non-group (group for users who are not teacher).
     * Then it adds those users into their respect groups.
     * @throws NoSuchAlgorithmException this exception is thrown by password service
     * @throws InvalidKeySpecException this exception is thrown by password service
     */
    @EventListener({ApplicationReadyEvent.class})
    public void addUsersToDatabase() throws NoSuchAlgorithmException, InvalidKeySpecException {
        var passwordHash = passwordService.hashPassword("qwerty123");
        List<UserRole> userRole = new ArrayList<>();
        userRole.add(UserRole.STUDENT);
        String pronouns = "His/Him";
        UserModel student =
                new UserModel(
                        "student",
                        passwordHash,
                        "Default",
                        "",
                        "Student",
                        "Def",
                        "I am a default student",
                        pronouns,
                        "default@student.com",
                        userRole,
                        currentTimestamp());

        userRepository.save(student);

        userRole.add(UserRole.TEACHER);
        UserModel teacher =
                new UserModel(
                        "teacher",
                        passwordHash,
                        "Default",
                        "",
                        "Teacher",
                        "",
                        "I am a default Teacher",
                        pronouns,
                        "default@teacher.com",
                        userRole,
                        currentTimestamp());
        userRepository.save(teacher);

        createTeacherAndNonGroup();
        addTeachersAndNonGroupMembers();
    }


    /**
     * This method creates 2 groups.
     */
    public void createTeacherAndNonGroup() {
        teachers = new GroupModel("Teachers", "Teaching Team");
        nonGroup = new GroupModel("Non Group", "Users without a group");
        groupRepository.save(teachers);
        groupRepository.save(nonGroup);

    }

    /**
     * This method adds users to the group based on their roles.
     */
    public void addTeachersAndNonGroupMembers() {
        List<Integer> areTeachers = new ArrayList<>();
        List<Integer> allOtherUsers = new ArrayList<>();

        Iterable<UserModel> allUsers = userRepository.findAll();
        allUsers.forEach(userModel -> {
            if(userModel.getRoles().contains(UserRole.TEACHER)) {
                areTeachers.add(userModel.getId());
            } else {
                allOtherUsers.add(userModel.getId());
            }
        });

        GroupMemberModel allTeachers = new GroupMemberModel(teachers.getId(), areTeachers);
        groupMemberRepository.save(allTeachers);
        GroupMemberModel notTeachers = new GroupMemberModel(nonGroup.getId(), allOtherUsers);
        groupMemberRepository.save(notTeachers);
    }

    /**
     * Helper function to get the current timestamp.
     *
     * @return the current timestamp
     */
    private static Timestamp currentTimestamp() {
        return Timestamp.newBuilder().setSeconds(Instant.now().getEpochSecond()).build();
    }
}
