package com.raf.usermanagement.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


import com.raf.usermanagement.enums.Status;
import com.raf.usermanagement.models.Machine;
import com.raf.usermanagement.models.User;
import com.raf.usermanagement.services.MachineService;
import com.raf.usermanagement.services.UserService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/api/machines")
public class MachineController {

    private final UserService userService;
    private final MachineService machineService;

    public MachineController(UserService userService, MachineService machineService) {
        this.userService = userService;
        this.machineService = machineService;
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllUserMachines() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userService.findByEmail(username);

        if (user.isPresent()) {
            return ResponseEntity.ok(machineService.getAllUserMachines());
        }
        return ResponseEntity.status(401).build();
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createMachine(@RequestBody HashMap<String, String> name) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userService.findByEmail(username);

        if (user.isPresent()) {
            User u = user.get();
            if (u.getPermission().getCanCreateMachine() == 1) {
                return ResponseEntity.ok(machineService.createMachine(name.get("name")));
            } else {
                return ResponseEntity.status(403).build();
            }
        }
        return ResponseEntity.status(401).build();
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteMachine(@PathVariable ("id") String id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userService.findByEmail(username);

        if (user.isPresent()) {
            User u = user.get();
            if (u.getPermission().getCanDestroyMachine() == 1) {
                Optional<Machine> machine = machineService.findById(Long.parseLong(id));
                if (!machine.isPresent()) {
                    return ResponseEntity.status(404).build();
                }
                Machine m = machine.get();
                if (m.isActive()) {
                    return ResponseEntity.status(400).body("Machine is under operation");
                }
                if (m.getStatus() == Status.STOPPED) {
                    if (m.isActive()) {
                        return ResponseEntity.status(400).body("Machine is under operation");
                    }
                    machineService.deleteMachine(Long.parseLong(id));
                    return ResponseEntity.ok().build();
                }
                return ResponseEntity.status(400).body("Machine is not stopped");
            } else {
                return ResponseEntity.status(403).build();
            }
        }
        return ResponseEntity.status(401).build();
    }

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> searchMachine(
        @RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "status", required = false) String status,
        @RequestParam(value = "dateFrom", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date dateFrom,
        @RequestParam(value = "dateTo", required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date dateTo
    ) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userService.findByEmail(username);
        
        if (user.isPresent()) {
            User u = user.get();
            if (u.getPermission().getCanSearchMachine() == 1) {
                if (status != null) {
                    String[] statusArr = status.split(",");
                    ArrayList<Status> statuses = new ArrayList<Status>();
                    for (String s : statusArr) {
                        statuses.add(Status.valueOf(s));
                    }
                    return ResponseEntity.ok(machineService.searchMachines(name, statuses, dateFrom, dateTo));
                }
                return ResponseEntity.ok(machineService.searchMachines(name, null, dateFrom, dateTo));
            } else {
                return ResponseEntity.status(403).build();
            }
        }
        return null;
    }

    @PatchMapping(value = "/start/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> startMachine(@PathVariable ("id") String id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userService.findByEmail(username);

        if (user.isPresent()) {
            User u = user.get();
            if (u.getPermission().getCanStartMachine() == 1) {

                if (machineService.isBusy(Long.parseLong(id))) {
                    return ResponseEntity.status(409).body("Machine is going through an operation");
                }

                if (machineService.canStartMachine(Long.parseLong(id))) {
                    machineService.startMachine(Long.parseLong(id));
                    return ResponseEntity.ok().build();
                }
                return ResponseEntity.status(400).body("Machine is not in a correct state");
            } else {
                return ResponseEntity.status(403).build();
            }
        }
        return ResponseEntity.status(401).build();
    }

    @PatchMapping(value = "/stop/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> stopMachine(@PathVariable ("id") String id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userService.findByEmail(username);

        if (user.isPresent()) {
            User u = user.get();
            if (u.getPermission().getCanStopMachine() == 1) {

                if (machineService.isBusy(Long.parseLong(id))) {
                    return ResponseEntity.status(409).body("Machine is going through an operation");
                }

                if (machineService.canStopMachine(Long.parseLong(id))) {
                    machineService.stopMachine(Long.parseLong(id));
                    return ResponseEntity.ok().build();
                }
                return ResponseEntity.status(400).body("Machine is not in a correct state");
            } else {
                return ResponseEntity.status(403).build();
            }
        }
        return ResponseEntity.status(401).build();
    }

    @PatchMapping(value = "/restart/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> restartMachine(@PathVariable ("id") String id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userService.findByEmail(username);

        if (user.isPresent()) {
            User u = user.get();
            if (u.getPermission().getCanRestartMachine() == 1) {

                if (machineService.isBusy(Long.parseLong(id))) {
                    return ResponseEntity.status(409).body("Machine is going through an operation");
                }

                if (machineService.canStopMachine(Long.parseLong(id))) {
                    machineService.restartMachine(Long.parseLong(id));
                    return ResponseEntity.ok().build();
                }
                return ResponseEntity.status(400).body("Machine is not in a correct state");
            } else {
                return ResponseEntity.status(403).build();
            }
        }
        return ResponseEntity.status(401).build();
    }

    @PatchMapping(value = "/schedule/stop/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> scheduleStopMachine(@PathVariable ("id") String id,
     @RequestBody Map<String, String> time) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userService.findByEmail(username);

        if (user.isPresent()) {
            User u = user.get();
            if (u.getPermission().getCanStopMachine() == 1) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                LocalDateTime dateTime = LocalDateTime.parse(time.get("time"), formatter);
                Optional<Machine> machine = machineService.findById(Long.parseLong(id));

                if (machine.isPresent()) {
                    Machine m = machine.get();
                    machineService.scheduleMachineStop(m.getId(), dateTime);
                    return ResponseEntity.ok().build();
                }
            } else {
                return ResponseEntity.status(403).build();
            }
        }
        return ResponseEntity.status(401).build();
    }

    @PatchMapping(value = "/schedule/start/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> scheduleStartMachine(@PathVariable ("id") String id,
     @RequestBody Map<String, String> time) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userService.findByEmail(username);

        if (user.isPresent()) {
            User u = user.get();
            if (u.getPermission().getCanStartMachine() == 1) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                LocalDateTime dateTime = LocalDateTime.parse(time.get("time"), formatter);
                Optional<Machine> machine = machineService.findById(Long.parseLong(id));

                if (machine.isPresent()) {
                    Machine m = machine.get();
                    machineService.scheduleMachineStart(m.getId(), dateTime);
                    return ResponseEntity.ok().build();
                }
            } else {
                return ResponseEntity.status(403).build();
            }
        }
        return ResponseEntity.status(401).build();
    }

    @PatchMapping(value = "/schedule/restart/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> scheduleRestartMachine(@PathVariable ("id") String id,
     @RequestBody Map<String, String> time) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userService.findByEmail(username);

        if (user.isPresent()) {
            User u = user.get();
            if (u.getPermission().getCanRestartMachine() == 1) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                LocalDateTime dateTime = LocalDateTime.parse(time.get("time"), formatter);
                Optional<Machine> machine = machineService.findById(Long.parseLong(id));

                if (machine.isPresent()) {
                    Machine m = machine.get();
                    machineService.scheduleMachineRestart(m.getId(), dateTime);
                    return ResponseEntity.ok().build();
                }
            } else {
                return ResponseEntity.status(403).build();
            }
        }
        return ResponseEntity.status(401).build();
    }

}
