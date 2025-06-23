package com.dotdot.marketplace.user.controller;


import com.dotdot.marketplace.user.dto.UserRequest;
import com.dotdot.marketplace.user.dto.UserResponse;
import com.dotdot.marketplace.user.service.UserService;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {


    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }



    @PostMapping()
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest userRequest) {
        UserResponse newUser = userService.createUser(userRequest);
        return ResponseEntity.ok(newUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable long id ) {
        UserResponse newUser = userService.getUserById(id);
        return ResponseEntity.ok(newUser);
    }
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable long id , @Valid @RequestBody UserRequest userRequest) {
        UserResponse newUser = userService.updateUser(id, userRequest);
        return ResponseEntity.ok(newUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserResponse> deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }




}
