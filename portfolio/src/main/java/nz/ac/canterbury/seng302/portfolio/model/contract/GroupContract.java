package nz.ac.canterbury.seng302.portfolio.model.contract;

import java.util.List;

/**
 * A contract relating for a group. Used for sending and retrieving groups from the database.
 *
 * @param id     the UID of the group
 * @param shortName   the short name of the group (e.g., "team 1000")
 * @param longName    the long name of the group (e.g., "Superstars")
 * @param users       the UID of all users currently belonging to that group
 */
public record GroupContract(
    int id,
    String shortName,
    String longName,
    List<UserContract> users
) implements Contractable {}
