package com.example.barbershop.service;

import com.example.barbershop.entity.AppointmentDatabaseEntity;
import com.example.barbershop.entity.AppointmentId;

import java.sql.Timestamp;
import java.util.List;

public interface AppointmentServiceInterface {

    List<AppointmentDatabaseEntity> findAll();

    AppointmentDatabaseEntity findById(AppointmentId id);

    AppointmentDatabaseEntity save(AppointmentDatabaseEntity appointmentDatabaseEntity);

    void deleteById(AppointmentId id);

    List<AppointmentDatabaseEntity> findByBarberId(Integer barberId);

    AppointmentDatabaseEntity findByBarberIdAndDate(Integer barberId, Timestamp date);

    void deleteAppointmentsOlderThan10Days();
}
