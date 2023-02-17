package com.raf.usermanagement.tasks;

import java.util.Date;
import java.util.Optional;

import com.raf.usermanagement.enums.Status;
import com.raf.usermanagement.models.ErrorMessage;
import com.raf.usermanagement.models.Machine;
import com.raf.usermanagement.models.User;
import com.raf.usermanagement.repositories.ErrorMessageRepository;
import com.raf.usermanagement.repositories.MachineRepository;
import com.raf.usermanagement.services.UserService;

public class StartMachineTask implements Runnable {
    
    private Long machineId;
    private Long userId;
    private final MachineRepository machineRepository;
    private final ErrorMessageRepository errorMessageRepository;
    private final UserService userService;

    public StartMachineTask(Long machineId, Long userId, MachineRepository machineRepository, ErrorMessageRepository errorMessageRepository, UserService userService) {
        this.machineId = machineId;
        this.userId = userId;
        this.machineRepository = machineRepository;
        this.errorMessageRepository = errorMessageRepository;
        this.userService = userService;
    }

    @Override
    public void run() {
        Optional<User> user = userService.findById(userId);

        System.out.println("Starting machine with id");

        if (user.isPresent()) {
            User u = user.get();
            Optional<Machine> machine = machineRepository.findByIdAndUserIdAndActive(machineId, u.getId(), true);

            if (machine.isPresent()) {
                Machine m = machine.get();
                System.out.println("Machine found");
                if (m.getStatus() == Status.RUNNING) {
                    ErrorMessage errorMessage = new ErrorMessage(new Date(), m.getId(), this.userId, "START", "Machine is already started");
                    errorMessageRepository.save(errorMessage);
                    System.out.println("Error message added to DB");
                    return;
                }

                if (m.isOperationActive()) {
                    errorMessageRepository.save(new ErrorMessage(new Date(), m.getId(), this.userId, "START", "Tried to start a machine that was undergoing an operation"));
                    System.out.println("Error message added to DB");
                } else {
                    try {
                        m.setOperationActive(true);
                        machineRepository.save(m);
                        System.out.println("Starting machine");
                        Thread.sleep((long) (Math.random() * (15000 - 10000) + 10000));
                    } catch (Exception e) {
                        ErrorMessage errorMessage = new ErrorMessage(new Date(), m.getId(), this.userId, "START", "Machine cannot be started");
                        errorMessageRepository.save(errorMessage);
                        System.out.println("Error message added to DB");
                    }
                    m = machineRepository.findById(m.getId()).get();
                    m.setStatus(Status.RUNNING);
                    m.setOperationActive(false);
                    machineRepository.save(m);
                    System.out.println("Machine scheduled start successful");
                }
            }
        }
    }
}
