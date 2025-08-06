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

@Entity
@Table(name = "appointments")
@IdClass(BarberId.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Appointment {
    
    @Id
    @Column(name = "date")
    private Timestamp date;

    @Column(name = "name")
    private String name;

    @Column(name = "phone")
    private String phone;

    @Id
    @Column(name = "id")
    private Long barberId;

    @Column(name = "comment")
    private String comment;
}
