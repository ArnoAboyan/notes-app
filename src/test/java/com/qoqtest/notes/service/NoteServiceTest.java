package com.qoqtest.notes.service;

import com.qoqtest.notes.dto.NoteFullResponseDTO;
import com.qoqtest.notes.dto.NoteRequestDTO;
import com.qoqtest.notes.dto.NoteShortResponseDTO;
import com.qoqtest.notes.entity.Note;
import com.qoqtest.notes.entity.NoteTag;
import com.qoqtest.notes.exception.NoteNotFoundException;
import com.qoqtest.notes.mapper.NoteMapper;
import com.qoqtest.notes.repository.NoteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private NoteMapper noteMapper;

    @InjectMocks
    private NoteService noteService;

    @Test
    @DisplayName("Should create note successfully")
    void createNote_Success() {
        NoteRequestDTO dto = new NoteRequestDTO("New Note", "Content", null);
        Note note = new Note();
        NoteFullResponseDTO expected = new NoteFullResponseDTO("1", "New Note", "Content", null, null);

        when(noteMapper.toEntity(dto)).thenReturn(note);
        when(noteRepository.save(note)).thenReturn(note);
        when(noteMapper.toFullResponseDTO(note)).thenReturn(expected);

        NoteFullResponseDTO result = noteService.createNote(dto);

        assertThat(result.title()).isEqualTo("New Note");
        verify(noteRepository).save(note);
    }

    @Test
    @DisplayName("Should return full note DTO when note exists by ID")
    void getNoteById_Exists_ReturnsDto() {
        String id = "123";
        Note note = new Note();
        note.setId(id);
        NoteFullResponseDTO expectedDto = new NoteFullResponseDTO(id, "Title", "Text", null, null);

        when(noteRepository.findById(id)).thenReturn(Optional.of(note));
        when(noteMapper.toFullResponseDTO(note)).thenReturn(expectedDto);

        NoteFullResponseDTO result = noteService.getNoteById(id);

        assertThat(result.title()).isEqualTo("Title");
    }

    @Test
    @DisplayName("Should throw NoteNotFound when note does not exist")
    void getNoteById_NotFound_ThrowsException() {
        String id = "non-existent";

        when(noteRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoteNotFoundException.class, () -> noteService.getNoteById(id));
    }

    @Test
    @DisplayName("Should correctly calculate word statistics")
    void getNoteWordFrequencyDescending_ValidText_ReturnsSortedMap() {
        String id = "1";
        Note note = new Note();
        note.setText("Java is cool, java is powerful!");

        when(noteRepository.findById(id)).thenReturn(Optional.of(note));

        Map<String, Long> stats = noteService.getNoteWordFrequencyDescending(id);

        assertThat(stats).hasSize(4).containsKeys("java", "is", "cool", "powerful");
        assertThat(stats.get("java")).isEqualTo(2L);
        assertThat(stats.get("cool")).isEqualTo(1L);

        assertThat(stats.keySet().iterator().next()).isIn("java", "is");
    }

    @Test
    @DisplayName("Should return empty map when note text is blank")
    void getNoteWordFrequencyDescending_EmptyText_ReturnsEmptyMap() {
        String id = "1";
        Note note = new Note();
        note.setText("   ");

        when(noteRepository.findById(id)).thenReturn(Optional.of(note));

        Map<String, Long> stats = noteService.getNoteWordFrequencyDescending(id);

        assertThat(stats).isEmpty();
    }

    @Test
    @DisplayName("Should throw NoteNotFound when calculating statistics for a non-existent note")
    void getNoteWordFrequencyDescending_NotFound_ThrowsException() {
        String id = "404-id";

        when(noteRepository.findById(id)).thenReturn(Optional.empty());

        NoteNotFoundException exception = assertThrows(NoteNotFoundException.class, () ->
                noteService.getNoteWordFrequencyDescending(id)
        );

        assertThat(exception.getMessage()).isEqualTo("Note not found with id: " + id);

        verify(noteRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Should delete note when it exists")
    void deleteNote_Exists_DeletesSuccessfully() {
        String id = "123";

        noteService.deleteNote(id);

        verify(noteRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Should update note when it exists")
    void updateNote_Exists_ReturnsUpdatedDto() {
        String id = "1";
        Note existingNote = new Note();
        NoteRequestDTO dto = new NoteRequestDTO("New Title", "New Text", null);
        NoteFullResponseDTO expectedResponse = new NoteFullResponseDTO(id, "New Title", "New Text", null, null);

        when(noteRepository.findById(id)).thenReturn(Optional.of(existingNote));
        when(noteRepository.save(existingNote)).thenReturn(existingNote);
        when(noteMapper.toFullResponseDTO(existingNote)).thenReturn(expectedResponse);

        NoteFullResponseDTO result = noteService.updateNote(id, dto);

        assertThat(result.title()).isEqualTo("New Title");

        verify(noteMapper).updateEntityFromDto(dto, existingNote);
    }

    @Test
    @DisplayName("Should throw NoteNotFound when updating a non-existent note")
    void updateNote_NotFound_ThrowsException() {

        String id = "999";
        NoteRequestDTO dto = new NoteRequestDTO("Updated Title", "Updated Content", null);

        when(noteRepository.findById(id)).thenReturn(Optional.empty());

        NoteNotFoundException exception = assertThrows(NoteNotFoundException.class, () ->
                noteService.updateNote(id, dto)
        );

        assertThat(exception.getMessage()).isEqualTo("Note not found with id: " + id);

        verify(noteRepository, never()).save(any(Note.class));
    }

    @Test
    @DisplayName("Should return paged notes without tags")
    void getAllNotes_NoTags_ReturnsPagedNotes() {

        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdDate").descending());
        Page<Note> notePage = new PageImpl<>(List.of(new Note()));

        when(noteRepository.findAll(pageable)).thenReturn(notePage);
        when(noteMapper.toShortResponseDTO(any(Note.class))).thenReturn(new NoteShortResponseDTO("1", "Title", null));

        Page<NoteShortResponseDTO> result = noteService.getAllNotes(null, 0, 10);

        assertThat(result).isNotEmpty();

        verify(noteRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Should return paged notes filtered by tags")
    void getAllNotes_WithTags_ReturnsFilteredPagedNotes() {

        Set<NoteTag> tags = Set.of(NoteTag.IMPORTANT);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdDate").descending());

        Note note = new Note();
        Page<Note> notePage = new PageImpl<>(List.of(note));

        when(noteRepository.findByTagsIn(tags, pageable)).thenReturn(notePage);

        when(noteMapper.toShortResponseDTO(any(Note.class)))
                .thenReturn(new NoteShortResponseDTO("1", "Title", Instant.now()));

        Page<NoteShortResponseDTO> result = noteService.getAllNotes(tags, 0, 10);

        assertThat(result).isNotEmpty();
        assertThat(result.getContent().getFirst().title()).isEqualTo("Title");
        verify(noteRepository).findByTagsIn(tags, pageable);
    }
}
