package com.raf.usermanagement.repositories;

import com.raf.usermanagement.models.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLoginRepository extends JpaRepository<User, String> {
    public User findByEmail(String email);
}
