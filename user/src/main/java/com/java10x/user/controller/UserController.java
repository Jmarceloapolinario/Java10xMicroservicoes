package com.java10x.user.controller;


import com.java10x.user.domain.UserModel;
import com.java10x.user.dto.UserDto;
import com.java10x.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping("/users")
    public ResponseEntity<UserModel> save(@RequestBody UserDto userDto){
        var userModel = new UserModel();
        BeanUtils.copyProperties(userDto, userModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.saveAndPublish(userModel));
    }

    @GetMapping("list/users")
    public ResponseEntity<List<UserModel>> getAllUsers(){
        return ResponseEntity.status(HttpStatus.OK).body(service.getAllUsers());
    }
    @GetMapping("/users/{email}")
    public ResponseEntity<Optional<UserModel>> getUserByEmail(@PathVariable String email){
        var userModelOptional = service.getUserByEmail(email);
        return ResponseEntity.ok(userModelOptional);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id){
        service.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
