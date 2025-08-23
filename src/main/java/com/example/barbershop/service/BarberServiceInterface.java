package com.example.barbershop.service;

import com.example.barbershop.entity.BarberDatabaseEntity;

import java.util.List;

public interface BarberServiceInterface {

    List<BarberDatabaseEntity> findAll();

    BarberDatabaseEntity findById(Integer id);

    BarberDatabaseEntity save(BarberDatabaseEntity barberDatabaseEntity);

    void deleteById(Integer id);
}
