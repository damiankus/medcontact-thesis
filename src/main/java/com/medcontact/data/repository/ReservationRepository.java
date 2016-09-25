package com.medcontact.data.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.medcontact.data.model.Reservation;

public interface ReservationRepository extends PagingAndSortingRepository<Reservation, Long> {

	public List<Reservation> findByDoctorId(long doctorId);
	public List<Reservation> findByPatientId(long patientId);
}
