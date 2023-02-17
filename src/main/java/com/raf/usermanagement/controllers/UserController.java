package com.raf.usermanagement.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.raf.usermanagement.models.User;
import com.raf.usermanagement.request.CreateUserRequest;
import com.raf.usermanagement.response.UserResponse;
import com.raf.usermanagement.services.UserService;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/api/users")
public class UserController {
    
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userService.findByEmail(username);

        if (user.isPresent()) {
            User u = user.get();
            if (u.getPermission().getCanReadUser() == 1) {
                List<User> userList = userService.findAll();
                List<UserResponse> returnList = new ArrayList<>();
                for (User toAdd: userList) {
                    returnList.add(new UserResponse(toAdd.getId(), toAdd.getName(), toAdd.getSurname(), toAdd.getEmail(), toAdd.getPermission()));
                }

                return ResponseEntity.ok(returnList);
            } else {
                return ResponseEntity.status(403).build();
            }
        }
        return ResponseEntity.status(401).build();
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest toCreateUser) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userService.findByEmail(username);

        if (user.isPresent()) {
            User u = user.get();

            if (u.getPermission().getCanCreateUser() == 1) {

                Optional<User> existingUser = userService.findByEmail(toCreateUser.getEmail());

                if (existingUser.isPresent()) {
                    return ResponseEntity.status(400).body("User with that email already exists");
                }

                User toAdd = new User(
                    toCreateUser.getEmail(),
                    toCreateUser.getName(),
                    toCreateUser.getSurname(),
                    this.passwordEncoder.encode(toCreateUser.getPassword()),
                    toCreateUser.getPermission()
                );

                userService.save(toAdd);

                UserResponse response = new UserResponse(toAdd.getName(), toAdd.getSurname(), toAdd.getEmail(), toAdd.getPermission());

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(403).build();
            }

        } else {
            return ResponseEntity.status(401).build();
        }

    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userService.findByEmail(username);

        if (user.isPresent()) {
            User u = user.get();

            if (u.getPermission().getCanDeleteUser() == 1) {
                Optional<User> toDelete = userService.findById(Long.parseLong(id));
                if (toDelete.isPresent()) {
                    userService.delete(toDelete.get().getId());
                    return ResponseEntity.noContent().build();
                } else {
                    return ResponseEntity.status(404).body("User with given email not found");
                }

            } else {
                return ResponseEntity.status(403).build();
            }
        }

        return ResponseEntity.status(401).build();
    }
    
    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userService.findByEmail(username);

        if (user.isPresent()) {
            User u = user.get();

            if (u.getPermission().getCanReadUser() == 1) {
                Optional<User> userWithId = userService.findById((Long.parseLong(id)));
                if (userWithId.isPresent()) {
                    return ResponseEntity.ok().body(
                        new UserResponse(
                            userWithId.get().getId(),
                            userWithId.get().getName(),
                            userWithId.get().getSurname(), 
                            userWithId.get().getEmail(), 
                            userWithId.get().getPermission())
                    );
                } else {
                    return ResponseEntity.notFound().build();
                }
            } else {
                return ResponseEntity.status(403).build();
            }
        }

        return ResponseEntity.status(401).build();
    }

    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody CreateUserRequest toCreateUser) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userService.findByEmail(username);

        if (user.isPresent()) {
            Optional<User> toEdit = userService.findById(Long.parseLong(id));
            if (toEdit.isPresent()) {

                if (user.get().getPermission().getCanUpdateUser() == 1) {
                    User edittedUser = toEdit.get();

                    edittedUser.setName(toCreateUser.getName());
                    edittedUser.setSurname(toCreateUser.getSurname());
                    edittedUser.setEmail(toCreateUser.getEmail());
                    edittedUser.setPermission(toCreateUser.getPermission());

                    userService.save(edittedUser);

                    return ResponseEntity.ok().body(new UserResponse(
                        edittedUser.getName(),
                        edittedUser.getSurname(), 
                        edittedUser.getEmail(), 
                        edittedUser.getPermission()));
                } else {
                    return ResponseEntity.status(403).build();
                }

            } else {
                return ResponseEntity.status(404).body("User with given email not found");
            }
        } 

        return ResponseEntity.status(401).build();
    }

}
