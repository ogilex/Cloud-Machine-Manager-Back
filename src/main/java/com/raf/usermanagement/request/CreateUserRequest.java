package com.raf.usermanagement.request;

import com.raf.usermanagement.models.Permission;

import lombok.Data;

@Data
public class CreateUserRequest {
    private String name;
    private String surname;
    private String email;
    private String password;
    private Permission permission;
}
