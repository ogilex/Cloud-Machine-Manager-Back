package com.raf.usermanagement.services;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface IService<User, Id> {
    User save(User user);
    List<User> findAll();
    Optional<User> findById(Id id);
    void delete(Id id);
    Optional<User> findByEmail(String email);
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
