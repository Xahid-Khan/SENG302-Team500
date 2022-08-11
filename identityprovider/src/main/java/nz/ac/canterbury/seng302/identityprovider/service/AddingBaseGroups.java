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
    public void addUsersToDatabase() {
        createTeacherAndNonGroup();
    }


    /**
     * This method creates 2 groups.
     */
    public void createTeacherAndNonGroup() {
        if (groupRepository.findByShortName("Teachers") == null) {
            teachers = new GroupModel("Teachers", "Teaching Team");
            nonGroup = new GroupModel("Non Group", "Users without a group");
            groupRepository.save(teachers);
            groupRepository.save(nonGroup);
            GroupMemberModel teacherGroupMembers = new GroupMemberModel(groupRepository.findByShortName("Teachers").getId(), List.of());
            GroupMemberModel nonGroupMembers = new GroupMemberModel(groupRepository.findByShortName("Non Group").getId(), List.of());
            groupMemberRepository.save(teacherGroupMembers);
            groupMemberRepository.save(nonGroupMembers);
        }
    }

}
