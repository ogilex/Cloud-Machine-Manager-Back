package com.raf.usermanagement.repositories;

import java.util.List;

import com.raf.usermanagement.models.ErrorMessage;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ErrorMessageRepository extends JpaRepository<ErrorMessage, Long> {
    List<ErrorMessage> findByUserId(Long userId);
}
