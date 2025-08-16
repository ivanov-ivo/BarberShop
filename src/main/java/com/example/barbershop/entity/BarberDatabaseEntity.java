package com.example.barbershop.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import lombok.Setter;
import lombok.NoArgsConstructor;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "barbers")
@Getter
@Setter
@NoArgsConstructor
public class BarberDatabaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;
    
    @Column(name = "photo")
    private String photo;

    @Column(name = "branch")
    private String branch;

    @Column(name = "information")
    private String information;

    public BarberDatabaseEntity(String name, String photo, String branch, String information) {
        this.name = name;
        this.photo = photo;
        this.branch = branch;
        this.information = information;
    }
}
