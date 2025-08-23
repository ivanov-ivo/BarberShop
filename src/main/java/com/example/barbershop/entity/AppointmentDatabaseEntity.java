package com.example.barbershop.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "appointments")
@IdClass(AppointmentId.class)
@AllArgsConstructor
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
}
