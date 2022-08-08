package nz.ac.canterbury.seng302.identityprovider.mapping;


import nz.ac.canterbury.seng302.identityprovider.database.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Provides utility methods for mapping between different representations of a GroupMember.
 */
@Component
public class GroupMemberMapper {

    @Autowired private GroupRepository groupRepository;


//    /**
//     * Map a GroupMemberModel from the database to a GetGroupDetailsResponse for sending to clients;
//     *
//     * @param user GroupMemberModel to map
//     * @return GetGroupDetailsResponse representing the given group
//     */
//    public GetGroupDetailsResponse toGetGroupDetailsResponse(Integer groupId, GroupMemberModel user) {
//
//        return GetGroupDetailsResponse.newBuilder()
//                .setShortName(groupRepository.findById(groupId).get().getShortName())
//                .setLongName(groupRepository.findById(groupId).get().getLongName())
//                .addAllMembers(user.getUserIdIterator())
//                .build();
//    }
}
