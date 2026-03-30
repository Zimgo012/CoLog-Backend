package com.zimgo.colog.user;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    public UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //GET all users
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    //GET user by id
    public User getUserById(Long id){

        boolean userExist = userRepository.existsById(id);
        if (!userExist){
            throw new RuntimeException("User does not exist!");
        }

        return userRepository.findById(id).get();
    }

    //CREATE user
    public User addUser (User user){
        boolean userExist = userRepository.existsByEmail(user.getEmail()) && userRepository.existByFirstName(user.getFirstName());

        if(userExist){
            throw new RuntimeException("User exist!");
        }

        return userRepository.save(user);
    }

    //DELETE user
    public void deleteUser(Long userId) {

       userRepository.deleteById(userId);
    }

    //PATCH user
    public User editUser(User user, Long id) {
        boolean userExist = userRepository.existsById(id);

        if(!userExist){
            throw new RuntimeException("User does not exist!");
        }
        User userFromDB = userRepository.findById(id).get(); //we can shorthand this

        //Setting new information
        userFromDB.setEmail((user.getEmail() != null) ? user.getEmail() : userFromDB.getEmail());
        userFromDB.setFirstName((user.getFirstName() != null) ? user.getFirstName() : userFromDB.getFirstName());
        userFromDB.setLastName((user.getLastName() != null) ? user.getLastName() : userFromDB.getLastName());

        return userRepository.save(userFromDB);

    }


}