package com.medcontact.data.model.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "shared_files")
@Data
@ToString(exclude={"reservation", "fileEntry"})

public class SharedFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long id;

    @ManyToOne
    @JoinColumn(name = "file_entry_id")
    private FileEntry fileEntry;

    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;
    
    @JsonValue
    public long getId() {
    	return this.id;
    }
}
