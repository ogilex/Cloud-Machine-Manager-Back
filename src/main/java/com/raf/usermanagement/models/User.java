package com.raf.usermanagement.models;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entity
@Table(name = "User")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String email;

    private String name;
    private String surname;

    @JsonIgnore
    private String password;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "can_create_user", column = @Column(name = "can_create_user")),
        @AttributeOverride(name = "can_read_user", column = @Column(name = "can_read_user")),
        @AttributeOverride(name = "can_update_user", column = @Column(name = "can_update_user")),
        @AttributeOverride(name = "can_delete_user", column = @Column(name = "can_delete_user")),
        @AttributeOverride(name = "can_search_machine", column = @Column(name = "can_search_machine")),
        @AttributeOverride(name = "can_start_machine", column = @Column(name = "can_start_machine")),
        @AttributeOverride(name = "can_stop_machine", column = @Column(name = "can_stop_machine")),
        @AttributeOverride(name = "can_restart_machine", column = @Column(name = "can_restart_machine")),
        @AttributeOverride(name = "can_create_machine", column = @Column(name = "can_create_machine")),
        @AttributeOverride(name = "can_destroy_machine", column = @Column(name = "can_destroy_machine"))
    })
    private Permission permission;

    public User() {

    }

    public User(String email, String name, String surname, String password, Permission permission) {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.permission = permission;
    }
    
}
