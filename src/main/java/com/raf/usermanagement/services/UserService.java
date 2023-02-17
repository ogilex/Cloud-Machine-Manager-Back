package com.raf.usermanagement.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.raf.usermanagement.models.User;
import com.raf.usermanagement.repositories.UserLoginRepository;
import com.raf.usermanagement.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IService<User, Long>, UserDetailsService {
    
    private final UserRepository userRepository;
    private final UserLoginRepository userLoginRepository;

    @Autowired
    public UserService(UserRepository userRepository, UserLoginRepository userLoginRepository) {
        this.userRepository = userRepository;
        this.userLoginRepository = userLoginRepository;
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> findAll() {
        return (List<User>)userRepository.findAll();
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        User nullUser = null;
        for (User user: userRepository.findAll()) {
            if (user.getEmail().equals(email)) return Optional.ofNullable(user);
        }
        return Optional.ofNullable(nullUser);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = this.userLoginRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User with the email: " + email + " not found");
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());
    }
}
