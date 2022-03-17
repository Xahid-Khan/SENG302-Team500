package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * A service that manages CRUD operations for View Account Controller.
 */

@Service
@Transactional
public class ViewAccountService {
    @Autowired
    private UserRepository userRepository;

    /**
     * Retrieves the details of a specific user.
     * @param id This is the Id of the user
     * @return A string containing all the details of a user.
     */
    public String get(String id) {
        var user = userRepository.findById(id).orElseThrow();
        return user.toString();
    }

}
