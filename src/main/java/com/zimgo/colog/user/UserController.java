package com.zimgo.colog.user;

import com.zimgo.colog.user.dto.UserRequest;
import jakarta.annotation.security.PermitAll;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.zimgo.colog.exception.RequestValidator.requireBody;


@RestController
@RequestMapping("/user")
public class UserController {

    public UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("get/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id){
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PatchMapping("/edit/{id}")
    public ResponseEntity<?> editUser(@RequestBody UserRequest req, @PathVariable Long id) {
        return ResponseEntity.ok(userService.editUser(requireBody(req), id));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id ){
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check/{username}")
    public ResponseEntity<?> checkUsername(@PathVariable String username){
        return ResponseEntity.ok(userService.findIfUsernameExist(username));
    }
}
