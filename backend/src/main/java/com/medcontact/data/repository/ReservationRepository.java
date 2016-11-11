package com.medcontact.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medcontact.data.model.domain.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

	public List<Reservation> findByDoctorId(long doctorId);
	public List<Reservation> findByPatientId(long patientId);
}
