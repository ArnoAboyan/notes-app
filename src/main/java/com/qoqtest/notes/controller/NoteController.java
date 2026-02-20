package com.qoqtest.notes.controller;

import com.qoqtest.notes.dto.NoteFullResponseDTO;
import com.qoqtest.notes.dto.NoteRequestDTO;
import com.qoqtest.notes.dto.NoteShortResponseDTO;
import com.qoqtest.notes.entity.NoteTag;
import com.qoqtest.notes.service.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @PostMapping
    @Operation(summary = "Create a new note", description = "Saves a note with title, text, and optional tags.")
    public ResponseEntity<NoteFullResponseDTO> create(@Valid @RequestBody NoteRequestDTO dto){
        return new ResponseEntity<>(noteService.createNote(dto), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List notes", description = "Returns a paginated list of notes showing only Title and Created Date.")
    public ResponseEntity<Page<NoteShortResponseDTO>> getAll(
            @RequestParam (required = false)Set<NoteTag> tags,
            @RequestParam (defaultValue = "0") int page,
            @RequestParam (defaultValue = "10") int size
            ) {
        return ResponseEntity.ok(noteService.getAllNotes(tags, page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get note details", description = "Retrieves the full content of a specific note, including its text and tags.")
    public ResponseEntity<NoteFullResponseDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(noteService.getNoteById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a note", description = "Fully updates an existing note. Requires all mandatory fields (title, text).")
    public ResponseEntity<NoteFullResponseDTO> update(
            @PathVariable String id,
            @Valid @RequestBody NoteRequestDTO dto) {
        return ResponseEntity.ok(noteService.updateNote(id, dto));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update a note", description = "Updates only the provided fields of an existing note.")
    public ResponseEntity<NoteFullResponseDTO> patch(@PathVariable String id, @RequestBody NoteRequestDTO dto) {
        return ResponseEntity.ok(noteService.updateNote(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a note",
            description = "Removes a note from the database by its unique ID. Returns 204 No Content on success."
    )
    public ResponseEntity<Void> delete(@PathVariable String id) {
        noteService.deleteNote(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/stats")
    @Operation(summary = "Get word statistics", description = "Calculates the frequency of unique words in a note, sorted descending.")
    public ResponseEntity<Map<String, Long>> getUniqueWordsByNote(@PathVariable String id) {
        Map<String, Long> statistics = noteService.getNoteWordFrequencyDescending(id);
        return ResponseEntity.ok(statistics);
    }

}
