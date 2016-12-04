package com.medcontact.data.model.builders;

import java.util.List;

import com.medcontact.data.model.domain.FileEntry;
import com.medcontact.data.model.domain.Patient;
import com.medcontact.data.model.domain.Reservation;

public class PatientBuilder extends BasicUserBuilder {
	public PatientBuilder() {
		this.user = new Patient();
	}

	public PatientBuilder setFiles(List<FileEntry> fileEntries) {
		((Patient) user).setFileEntries(fileEntries);
		return this;
	}

	public PatientBuilder setReservations(List<Reservation> reservations) {
		((Patient) user).setReservations(reservations);
		return this;
	}
}