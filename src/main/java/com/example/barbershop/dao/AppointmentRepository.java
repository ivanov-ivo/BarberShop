package com.example.barbershop.dao;

import com.example.barbershop.entity.AppointmentDatabaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.barbershop.entity.AppointmentId;
import org.springframework.data.jpa.repository.Modifying;

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentDatabaseEntity, AppointmentId> {

    List<AppointmentDatabaseEntity> findByBarberId(@Param("barberId") Integer barberId);

    AppointmentDatabaseEntity findByBarberIdAndDate(@Param("barberId") Integer barberId, @Param("date") Timestamp date);

    @Query("DELETE FROM AppointmentDatabaseEntity a WHERE a.date < :cutoff")
    @Modifying
    void deleteAppointmentsOlderThan(@Param("cutoff") Timestamp cutoff);
}
