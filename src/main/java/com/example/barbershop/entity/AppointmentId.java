package com.example.barbershop.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class AppointmentId implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Timestamp date;
    private Integer barberId;
    
    // Defensive copy for getter to prevent exposure of internal representation
    public Timestamp getDate() {
        return date != null ? new Timestamp(date.getTime()) : null;
    }
    
    // Defensive copy for setter to prevent exposure of internal representation
    public void setDate(Timestamp date) {
        this.date = date != null ? new Timestamp(date.getTime()) : null;
    }
    
    // Custom constructor to prevent exposure of internal representation
    public AppointmentId(Timestamp date, Integer barberId) {
        this.date = date != null ? new Timestamp(date.getTime()) : null;
        this.barberId = barberId;
    }
} 