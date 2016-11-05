package com.medcontact.data.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medcontact.data.model.domain.Note;

public interface NoteRepository extends JpaRepository<Note, Long> {
	public Optional<Note> findNoteById(Long id);

}
