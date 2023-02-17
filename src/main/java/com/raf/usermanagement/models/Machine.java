package com.raf.usermanagement.models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.raf.usermanagement.enums.Status;

import lombok.Data;

@Data
@Entity
@Table(name = "Machine")
public class Machine {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdBy")
    @JsonIgnore
    private User user;

    private boolean active;

    private Date createdAt;

    private boolean operationActive;

    @Version
    @JsonIgnore
    private Long version;

}
