package com.example.barbershop.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "appointments")
@IdClass(AppointmentId.class)
@NoArgsConstructor
@Getter
@Setter
public class AppointmentDatabaseEntity {
    
    @Id
    @Column(name = "date")
    private Timestamp date;

    @Column(name = "name")
    private String name;

    @Column(name = "phone")
    private String phone;

    @Id
    @Column(name = "id")
    private Integer barberId;

    @Column(name = "comment")
    private String comment;
    
    // Defensive copy for getter to prevent exposure of internal representation
    public Timestamp getDate() {
        return date != null ? new Timestamp(date.getTime()) : null;
    }
    
    // Defensive copy for setter to prevent exposure of internal representation
    public void setDate(Timestamp date) {
        this.date = date != null ? new Timestamp(date.getTime()) : null;
    }
    
    // Custom constructor to prevent exposure of internal representation
    public AppointmentDatabaseEntity(Timestamp date, String name, String phone, Integer barberId, String comment) {
        this.date = date != null ? new Timestamp(date.getTime()) : null;
        this.name = name;
        this.phone = phone;
        this.barberId = barberId;
        this.comment = comment;
    }
}
