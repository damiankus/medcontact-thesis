package com.medcontact.data.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.medcontact.data.model.Doctor;
import com.medcontact.data.model.Note;
import com.medcontact.data.model.Opinion;
import com.medcontact.data.model.Reservation;
import com.medcontact.data.model.ScheduleTimeSlot;
import com.medcontact.data.model.Specialty;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
	public Optional<Doctor> findByUsername(String username);
	
	@Query("SELECT doctor.opinions FROM Doctor doctor WHERE id = :id")
	public List<Opinion> findOpinionsByDoctorId(@Param("id") Long doctorId);
	
	@Query("SELECT doctor.reservations FROM Doctor doctor WHERE id = :id")
	public List<Reservation> findReservationsByDoctorId(@Param("id") Long doctorId);
	
	@Query("SELECT doctor.specialties FROM Doctor doctor WHERE id = :id")
	public List<Specialty> findSpecialtiesByDoctorId(@Param("id") Long doctorId);
	
	@Query("SELECT doctor.weeklySchedule FROM Doctor doctor WHERE id = :id")
	public List<ScheduleTimeSlot> findWeeklyScheduleByDoctorId(@Param("id") Long doctorId);
	
	@Query("SELECT doctor.notes FROM Doctor doctor WHERE id = :id")
	public List<Note> findNotesByDoctorId(@Param("id") Long doctorId);
}
