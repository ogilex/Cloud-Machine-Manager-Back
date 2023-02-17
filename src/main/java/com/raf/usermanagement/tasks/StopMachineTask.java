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

public class StopMachineTask implements Runnable {
    
    private Long machineId;
    private Long userId;
    private final MachineRepository machineRepository;
    private final ErrorMessageRepository errorMessageRepository;
    private final UserService userService;

    public StopMachineTask(Long machineId, Long userId, MachineRepository machineRepository, ErrorMessageRepository errorMessageRepository, UserService userService) {
        this.machineId = machineId;
        this.userId = userId;
        this.machineRepository = machineRepository;
        this.errorMessageRepository = errorMessageRepository;
        this.userService = userService;
    }

    @Override
    public void run() {
        Optional<User> user = userService.findById(userId);

        System.out.println("Stopping machine with id");

        if (user.isPresent()) {
            User u = user.get();
            Optional<Machine> machine = machineRepository.findByIdAndUserIdAndActive(machineId, u.getId(), true);

            if (machine.isPresent()) {
                Machine m = machine.get();
                System.out.println("Machine found");

                if (m.getStatus() == Status.STOPPED) {
                    ErrorMessage errorMessage = new ErrorMessage(new Date(), m.getId(), this.userId, "STOP", "Machine is already stopped");
                    errorMessageRepository.save(errorMessage);
                    System.out.println("Error message added to DB");
                    return;
                }

                if (m.isOperationActive()) {
                    errorMessageRepository.save(new ErrorMessage(new Date(), m.getId(), this.userId, "STOP", "Tried to stop a machine that was undergoing an operation"));
                    System.out.println("Error message added to DB");
                } else {
                    try {
                        m.setOperationActive(true);
                        machineRepository.save(m);
                        System.out.println("Stopping machine");
                        Thread.sleep((long) (Math.random() * (15000 - 10000) + 10000));
                    } catch (Exception e) {
                        ErrorMessage errorMessage = new ErrorMessage(new Date(), m.getId(), this.userId, "STOP", "Machine cannot be stopped");
                        errorMessageRepository.save(errorMessage);
                        System.out.println("Error message added to DB");
                    }
                    m = machineRepository.findById(m.getId()).get();
                    m.setStatus(Status.STOPPED);
                    m.setOperationActive(false);
                    machineRepository.save(m);
                    System.out.println("Machine scheduled stop successful");
                }
            }
        }
    }
}
