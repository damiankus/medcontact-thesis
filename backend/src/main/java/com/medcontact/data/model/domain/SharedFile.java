package com.medcontact.data.model.domain;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "shared_files")
@Data

public class SharedFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long id;

    @ManyToOne
    @JoinColumn(name = "file_entry_id")
    private FileEntry fileEntry;

    private LocalDateTime expirationTime;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

}
