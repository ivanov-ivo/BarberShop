package com.example.barbershop.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.barbershop.entity.Appointment;
import java.sql.Timestamp;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.barbershop.entity.BarberId;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, BarberId> {

    @Query("SELECT a FROM Appointment a WHERE a.barberId = :barberId")
    List<Appointment> findByBarberId(@Param("barberId") Long barberId);

    @Query("SELECT a FROM Appointment a WHERE a.barberId = :barberId AND a.date = :date")
    Appointment findByBarberIdAndDate(@Param("barberId") Long barberId, @Param("date") Timestamp date);

    @Query("DELETE FROM Appointment a WHERE a.date < :cutoff")
    @Modifying
    @Transactional
    void deleteAppointmentsOlderThan(@Param("cutoff") Timestamp cutoff);
}
