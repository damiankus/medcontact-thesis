package com.medcontact.data.validation;

import com.medcontact.data.model.Doctor;

public class DoctorValidator extends DataValidatorHelper<Doctor> {
	public final int MAX_UNIVERSITY_LEN = 128;
	public final int MAX_BIOGRAPHY_LEN = 2048;
	public final int MAX_TITLE_LEN = 128;
	public final int MAX_ROOMID_LEN = 36;
	
	public final int MAX_SPECIALTIES_SIZE = Integer.MAX_VALUE;
	public final int MAX_OPINIONS_SIZE = Integer.MAX_VALUE;
	public final int MAX_RESERVATIONS_SIZE = Integer.MAX_VALUE;
	public final int MAX_SCHEDULE_SIZE = Integer.MAX_VALUE;
	public final int MAX_NOTES_SIZE = Integer.MAX_VALUE;

	private BasicUserValidator userValidator = new BasicUserValidator();
	private NoteValidator noteValidator = new NoteValidator();
	private OpinionValidator opinionValidator = new OpinionValidator();
	private ReservationValidator reservationValidator = new ReservationValidator();
	private ScheduleTimeSlotValidator scheduleTimeSlotValidator = new ScheduleTimeSlotValidator();
	
	@Override
	public boolean validate(Doctor doctor) {
		return userValidator.validate(doctor)
				&& isStringLengthValid(doctor.getUniversity(), MAX_UNIVERSITY_LEN)
				&& isStringLengthValid(doctor.getBiography(), MAX_BIOGRAPHY_LEN)
				&& isStringLengthValid(doctor.getTitle(), MAX_TITLE_LEN)
				&& isStringLengthValid(doctor.getRoomId(), MAX_ROOMID_LEN)
				&& isListValid(doctor.getNotes(), MAX_NOTES_SIZE, noteValidator)
				&& isListValid(doctor.getOpinions(), MAX_OPINIONS_SIZE, opinionValidator)
				&& isListValid(doctor.getReservations(), MAX_RESERVATIONS_SIZE, reservationValidator)
				&& isListValid(doctor.getWeeklySchedule(), MAX_SCHEDULE_SIZE, scheduleTimeSlotValidator);
	}
}
