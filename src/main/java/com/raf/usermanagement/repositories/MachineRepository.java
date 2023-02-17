package com.raf.usermanagement.repositories;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.raf.usermanagement.enums.Status;
import com.raf.usermanagement.models.Machine;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MachineRepository extends JpaRepository<Machine, Long> {
    List<Machine> findByUserId(Long userId);
    List<Machine> findByUserIdAndActive(Long userId, boolean active);
    List<Machine> findByUserIdAndNameContainingAndStatusInAndCreatedAtBetweenAndActive(Long userId, String name, List<Status> statuses, Date start, Date end, boolean active);
    List<Machine> findByUserIdAndNameContainingAndActive(Long userId, String name, boolean active);
    List<Machine> findByUserIdAndStatusInAndActive(Long userId, List<Status> statuses, boolean active);
    List<Machine> findByUserIdAndNameContainingAndStatusInAndActive(Long userId, String name, List<Status> statuses, boolean active);
    List<Machine> findByUserIdAndNameContainingAndCreatedAtBetweenAndActive(Long userId, String name, Date start, Date end, boolean active);
    List<Machine> findByUserIdAndStatusInAndCreatedAtBetweenAndActive(Long userId, List<Status> statuses, Date start, Date end, boolean active);
    List<Machine> findByUserIdAndCreatedAtBetweenAndActive(Long userId, Date start, Date end, boolean active);
    Optional<Machine> findByIdAndUserIdAndActive(Long id, Long userId, boolean active);
}
