package com.medcontact.data.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.medcontact.data.model.domain.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

	public List<Reservation> findByDoctorId(long doctorId);
	public List<Reservation> findByPatientId(long patientId);
	
	@Query("FROM Reservation WHERE startDateTime > :now ORDER BY startDateTime")
	public List<Reservation> findNextReservations(@Param("now") LocalDateTime now);
}
