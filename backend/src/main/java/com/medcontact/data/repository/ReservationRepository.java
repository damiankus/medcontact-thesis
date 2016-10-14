package com.medcontact.data.repository;

import java.util.List;


import com.medcontact.data.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

	public List<Reservation> findByDoctorId(long doctorId);
	public List<Reservation> findByPatientId(long patientId);
}
