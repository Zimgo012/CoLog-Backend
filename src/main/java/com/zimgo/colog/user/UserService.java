package com.zimgo.colog.user;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    public UserRepository userRepository;

    public List<User> findAll(){ return userRepository.findAll(); }

    public void addUser (User user){
        boolean userExist = userRepository.existsByEmail(user.getEmail()) && userRepository.existByFirstName(user.getFirstName());

        if(userExist){
            //throw error
        }

        userRepository.save(user);
    }
    
    public void deleteUser(User user) {
        boolean userExist = userRepository.existsById(user.getId());

        if(!userExist){
            //throw error
        }

        userRepository.delete(user);
    }

    public void editUser(User user) {
        boolean userExist = userRepository.existsById(user.getId());

        if(!userExist){
            //throw error
        }
        User userFromDB = userRepository.findById(user.getId()).get(); //we can shorthand this

        //Setting new information
        userFromDB.setEmail((user.getEmail() != null) ? user.getEmail() : userFromDB.getEmail());
        userFromDB.setFirstName((user.getFirstName() != null) ? user.getFirstName() : userFromDB.getFirstName());
        userFromDB.setLastName((user.getLastName() != null) ? user.getLastName() : userFromDB.getLastName());

    }
}