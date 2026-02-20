package com.qoqtest.notes.service;

import com.qoqtest.notes.dto.NoteFullResponseDTO;
import com.qoqtest.notes.dto.NoteRequestDTO;
import com.qoqtest.notes.dto.NoteShortResponseDTO;
import com.qoqtest.notes.entity.NoteTag;
import com.qoqtest.notes.entity.Note;
import com.qoqtest.notes.exception.NoteNotFoundException;
import com.qoqtest.notes.mapper.NoteMapper;
import com.qoqtest.notes.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NoteService {

    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;

    @Transactional
    public NoteFullResponseDTO createNote(NoteRequestDTO dto){
        log.debug("Creating a new note with title: {}", dto.title());
        Note note = noteMapper.toEntity(dto);
        Note saveNote = noteRepository.save(note);
        log.info("Created new note with title: {}", dto.title());
        return noteMapper.toFullResponseDTO(saveNote);
    }

    @Transactional
    public void deleteNote (String id){
        log.info("Attempting to delete note with id: {}", id);

        noteRepository.deleteById(id);

        log.info("Successfully deleted note with id: {}", id);
    }

    public NoteFullResponseDTO getNoteById(String id) {
        log.info("Fetching note with id: {}", id);

        return noteRepository.findById(id)
                .map(note -> {
                    log.debug("Note found in database: {}", note.getTitle());
                    return noteMapper.toFullResponseDTO(note);
                })
                .orElseThrow(() -> {
                    log.warn("Note not found with id: {}", id);
                    return new NoteNotFoundException("Note not found with id: " + id);
                });
    }

    public Page<NoteShortResponseDTO> getAllNotes(Set<NoteTag> tags, int page, int size) {
        log.info("Request to get notes page: {}, size: {}, filtering by tags: {}", page, size, tags);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());

        Page<Note> notesPage;

        if (tags == null || tags.isEmpty()) {
            log.debug("No tags provided, fetching all notes with pagination");
            notesPage = noteRepository.findAll(pageable);
        } else {
            log.debug("Filtering notes by tags: {}", tags);
            notesPage = noteRepository.findByTagsIn(tags, pageable);
        }

        log.info("Found {} notes on current page. Total elements in DB: {}",
                notesPage.getNumberOfElements(), notesPage.getTotalElements());

        return notesPage.map(noteMapper::toShortResponseDTO);
    }

    @Transactional
    public NoteFullResponseDTO updateNote(String id, NoteRequestDTO dto) {
        log.info("Attempting to update note with id: {}", id);

        Note existingNote = noteRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Update failed: Note with id {} not found", id);
                    return new NoteNotFoundException("Note not found with id: " + id);
                });


        log.debug("Original note state: title='{}', tags={}", existingNote.getTitle(), existingNote.getTags());

        noteMapper.updateEntityFromDto(dto, existingNote);

        Note updatedNote = noteRepository.save(existingNote);

        log.info("Successfully updated note with id: {}. New title: '{}'", id, updatedNote.getTitle());

        return noteMapper.toFullResponseDTO(updatedNote);
    }

    public Map<String, Long> getNoteWordFrequencyDescending(String id) {
        log.info("Calculating word statistics for note id: {}", id);

        Note note = noteRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Statistics calculation failed: Note with id {} not found", id);
                    return new NoteNotFoundException("Note not found with id: " + id);
                });

        String text = note.getText();
        if (text == null || text.isBlank()) {
            log.info("Note with id {} has no text content. Returning empty statistics.", id);
            return Collections.emptyMap();
        }

        log.debug("Processing text of length: {} characters", text.length());

        Map<String, Long> statistics = Arrays.stream(text.toLowerCase()
                        .split("\\P{L}+"))
                .filter(word -> !word.isEmpty())
                .collect(Collectors.groupingBy(word -> word, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        log.info("Statistics calculated successfully for note {}. Unique words found: {}", id, statistics.size());

        return statistics;
    }

}
