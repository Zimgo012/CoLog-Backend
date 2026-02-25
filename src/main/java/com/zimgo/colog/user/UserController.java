package com.zimgo.colog.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Controller
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

    private UserService userService;

    @PostMapping("/add")
    public void addUser(User user){ userService.addUser(user); }

    @GetMapping("/all")
    public List<User> getAllUsers(){ return userService.findAll();}

    @PatchMapping("/edit")
    public void editUser(User user){ userService.editUser(user); }

    @DeleteMapping("/delete")
    public void deleteUser(User user){ userService.deleteUser(user); }
}
