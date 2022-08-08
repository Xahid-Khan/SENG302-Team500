package nz.ac.canterbury.seng302.identityprovider.service;

import nz.ac.canterbury.seng302.identityprovider.database.GroupMemberModel;
import nz.ac.canterbury.seng302.identityprovider.database.GroupMemberRepository;
import nz.ac.canterbury.seng302.identityprovider.database.GroupModel;
import nz.ac.canterbury.seng302.identityprovider.database.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddingBaseGroups {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    private GroupModel teachers;
    private GroupModel nonGroup;


    public void CreateTeacherAndNonGroup() {
        teachers = new GroupModel("Teaching Staff", "This group contain all the members who are teachers");
        nonGroup = new GroupModel("Non Group", "This group contain all the members who are not part of any other groups");
        groupRepository.save(teachers);
        groupRepository.save(nonGroup);

    }

    public void addTeachersAndNonGroupMembers() {
        //TODO
    }

}
