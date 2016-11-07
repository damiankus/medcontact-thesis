package com.medcontact.data.model.builders;

import com.medcontact.data.model.domain.*;

import java.util.List;

public class DoctorBuilder extends BasicUser.BasicUserBuilder {
    public static DoctorBuilder getBuilder() {
        return new DoctorBuilder();
    }

    public DoctorBuilder() {
        this.user = new Doctor();
    }

    public DoctorBuilder setTitle(String title) {
        ((Doctor) user).setTitle(title);
        return this;
    }

    public DoctorBuilder setUniversity(String university) {
        ((Doctor) user).setUniversity(university);
        return this;
    }

    public DoctorBuilder setBiography(String biography) {
        ((Doctor) user).setBiography(biography);
        return this;
    }

    public DoctorBuilder setSpecialties(List<Specialty> specialties) {
        ((Doctor) user).setSpecialties(specialties);
        return this;
    }

    public DoctorBuilder setOpinions(List<Opinion> opinions) {
        ((Doctor) user).setOpinions(opinions);
        return this;
    }

    public DoctorBuilder setReservations(List<Reservation> reservations) {
        ((Doctor) user).setReservations(reservations);
        return this;
    }

    public DoctorBuilder setWeeklySchedule(List<ScheduleTimeSlot> schedule) {
        ((Doctor) user).setWeeklySchedule(schedule);
        return this;
    }

    public DoctorBuilder setRoomId(String roomId) {
        ((Doctor) user).setRoomId(roomId);
        return this;
    }

    public DoctorBuilder setBusy(boolean busy) {
        ((Doctor) user).setBusy(busy);
        return this;
    }

    public DoctorBuilder setNotes(List<Note> notes) {
        ((Doctor) user).setNotes(notes);
        return this;
    }
}
