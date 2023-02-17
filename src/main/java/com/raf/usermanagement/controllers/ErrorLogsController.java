package com.raf.usermanagement.controllers;

import java.util.Optional;

import com.raf.usermanagement.models.User;
import com.raf.usermanagement.repositories.ErrorMessageRepository;
import com.raf.usermanagement.services.UserService;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/api/logs")
public class ErrorLogsController {

  private final ErrorMessageRepository errorMessageRepository;
  private final UserService userService;

  public ErrorLogsController(ErrorMessageRepository errorMessageRepository, UserService userService) {
    this.errorMessageRepository = errorMessageRepository;
    this.userService = userService;
  }

  @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getAll() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    Optional<User> user = userService.findByEmail(username);

    if (user.isPresent()) {
      return ResponseEntity.ok(errorMessageRepository.findByUserId(user.get().getId()));
    } else {
      return ResponseEntity.badRequest().build();
    }

  }

}
