package com.raf.usermanagement.response;

import com.raf.usermanagement.models.Permission;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private Permission permission;

    public UserResponse(String name, String surname, String email, Permission permission) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.permission = permission;
    }

    public UserResponse(Long id, String name, String surname, String email, Permission permission) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.permission = permission;
    }


}
