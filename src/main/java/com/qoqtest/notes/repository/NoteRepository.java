package com.qoqtest.notes.repository;

import com.qoqtest.notes.entity.NoteTag;
import com.qoqtest.notes.entity.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface NoteRepository extends MongoRepository<Note, String> {

    Page<Note> findByTagsIn(Set<NoteTag> tags, Pageable pageable);
}

