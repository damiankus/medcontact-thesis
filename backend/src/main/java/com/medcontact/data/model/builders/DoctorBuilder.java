package com.medcontact.data.model.builders;

import java.util.List;

import com.medcontact.data.model.domain.Doctor;
import com.medcontact.data.model.domain.Note;
import com.medcontact.data.model.domain.Reservation;
import com.medcontact.data.model.domain.Specialty;

public class DoctorBuilder extends BasicUserBuilder {
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

    public DoctorBuilder setReservations(List<Reservation> reservations) {
        ((Doctor) user).setReservations(reservations);
        return this;
    }

    public DoctorBuilder setRoomId(String roomId) {
        ((Doctor) user).setRoomId(roomId);
        return this;
    }

    public DoctorBuilder setAvailable(boolean available) {
        ((Doctor) user).setAvailable(available);
        return this;
    }

    public DoctorBuilder setNotes(List<Note> notes) {
        ((Doctor) user).setNotes(notes);
        return this;
    }
}
