package com.raf.usermanagement.models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Entity
@Data
@Table(name = "error_message")
public class ErrorMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Date errorDate;

    private Long machineId;

    @JsonIgnore
    private Long userId;
    
    private String operationName;
    private String errorMessage;

    public ErrorMessage(Date errorDate, Long machineId, Long userId, String operationName, String errorMessage) {
        this.errorDate = errorDate;
        this.machineId = machineId;
        this.userId = userId;
        this.operationName = operationName;
        this.errorMessage = errorMessage;
    }

    public ErrorMessage() {
    }
}
