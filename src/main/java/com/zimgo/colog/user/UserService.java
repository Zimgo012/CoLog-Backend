package com.zimgo.colog.user;

import com.zimgo.colog.auth.security.CustomUserDetails;
import com.zimgo.colog.user.dto.UserRequest;
import com.zimgo.colog.user.dto.UserResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.security.access.AccessDeniedException;
import java.util.List;

/**
 * The UserService class provides services for managing user accounts, including
 * operations such as retrieving user details, creating new users, updating user
 * information, and deleting users from the repository. It serves as a layer that
 * facilitates interactions between the controller and the UserRepository.
 *
 * The primary responsibilities of this service include:
 * - Managing user retrieval based on various attributes such as ID, email, and username.
 * - Adding new users while enforcing constraints on unique identifiers.
 * - Allowing authenticated users to update their own account details.
 * - Providing secure password encoding for user details.
 * - Deleting users upon request.
 *
 * This service relies on dependency injection to access the UserRepository for
 * database operations and the PasswordEncoder for securely handling passwords.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** GET all users
     * Retrieves a list of all users from the repository.
     *
     * @return a list of User objects representing all users stored in the repository
     */
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    /** GET user by id
     * Retrieves a user by their unique identifier.
     *
     * @param id the unique identifier of the user to retrieve
     * @return the User object corresponding to the provided ID
     * @throws RuntimeException if the user with the given ID does not exist
     */
    public User getUserById(Long id){

        boolean userExist = userRepository.existsById(id);
        if (!userExist){
            throw new RuntimeException("User does not exist!");
        }

        return userRepository.findById(id).get();
    }

    /** GET User by email
     * Retrieves a user by their email address.
     *
     * @param email the email address of the user to retrieve
     * @return the User object corresponding to the given email address
     * @throws RuntimeException if no user exists with the specified email
     */
    public User getUserByEmail(String email){


        boolean userExist = userRepository.existsByEmail(email);
        if (!userExist){
            throw new RuntimeException("User does not exist");
        }

        return userRepository.findByEmail(email);
    }

    /** GET USER by username
     * Retrieves a user by their username.
     *
     * @param username the username of the user to retrieve
     * @return the User object corresponding to the provided username
     * @throws RuntimeException if no user exists with the specified username
     */
    public User getUserByUsername(String username){
        boolean userExist = userRepository.existByUsername(username);
        if (!userExist){
            throw new RuntimeException("User does not exist");
        }

        return userRepository.findByUsername(username);
    }

    /** CREATE user
     * Adds a new user to the repository if no existing user has the same email and first name.
     *
     * @param user the User object to be added
     * @return the saved User object
     * @throws RuntimeException if a user with the same email and first name already exists
     */
    public User addUser (User user) {
        boolean userExist = userRepository.existsByEmail(user.getEmail()) && userRepository.existByFirstName(user.getFirstName());

        if (userExist) {
            throw new RuntimeException("User exist!");
        }

        return userRepository.save(user);
    }

    /** DELETE user by Id
     * Deletes a user from the repository based on their unique identifier.
     *
     * @param userId the unique identifier of the user to be deleted
     */
    public void deleteUser(Long userId) {

       userRepository.deleteById(userId);
    }

    /** EDIT user by id
     * Updates the details of an existing user. Only the authenticated user can
     * edit their own account information.
     *
     * @param req the UserRequest object containing the updated user details
     * @param id the unique identifier of the user to be edited
     * @return a UserResponse object with the updated user details
     * @throws AccessDeniedException if the authenticated user attempts to edit another user's account
     * @throws RuntimeException if the*/
    public UserResponse editUser(UserRequest req, Long id) throws AccessDeniedException {

        boolean userExist = userRepository.existsById(id);
        if(!userExist){
            throw new RuntimeException("User does not exist!");
        }


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Long authenticatedUserId = userDetails.getId();

        User userFromDB = userRepository.findById(id).get(); //we can shorthand this

        if (!authenticatedUserId.equals(id)) {
            throw new AccessDeniedException(
                    "Can only edit your own account"
            );
        }

        //Setting new information
        userFromDB.setEmail((req.getEmail() != null) ? req.getEmail() : userFromDB.getEmail());
        userFromDB.setFirstName((req.getFirstName() != null) ? req.getFirstName() : userFromDB.getFirstName());
        userFromDB.setLastName((req.getLastName() != null) ? req.getLastName() : userFromDB.getLastName());

        if (req.getPassword() != null){
            userFromDB.setPassword(passwordEncoder.encode(req.getPassword()));
        }

         userRepository.save(userFromDB);

        return new UserResponse(userFromDB.getFirstName(), userFromDB.getLastName(), userFromDB.getEmail());
    }

    public Boolean findIfUsernameExist(String username){
        return userRepository.existByUsername(username);
    }
}