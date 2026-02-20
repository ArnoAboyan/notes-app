package com.qoqtest.notes.controller;

import com.qoqtest.notes.dto.NoteFullResponseDTO;
import com.qoqtest.notes.dto.NoteRequestDTO;
import com.qoqtest.notes.service.NoteService;
import com.qoqtest.notes.exception.NoteNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(NoteController.class)
class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NoteService noteService;

    @Test
    @DisplayName("POST /api/notes - Success")
    void createNote_ValidRequest_ReturnsCreated() throws Exception {
        NoteFullResponseDTO response = new NoteFullResponseDTO("1", "Title", "Text", null, null);

        when(noteService.createNote(any(NoteRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Title",
                                    "text": "Text"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.title").value("Title"));
    }

    @Test
    @DisplayName("DELETE /api/notes/{id} - Success")
    void deleteNote_ReturnsNoContent() throws Exception {
        String id = "123";
        mockMvc.perform(delete("/api/notes/" + id))
                .andExpect(status().isNoContent());
        
        verify(noteService).deleteNote(id);
    }

    @Test
    @DisplayName("GET /api/notes/{id}/stats - Success")
    void getStats_ReturnsMap() throws Exception {
        Map<String, Long> stats = Map.of("java", 2L, "spring", 1L);
        when(noteService.getNoteWordFrequencyDescending("1")).thenReturn(stats);

        mockMvc.perform(get("/api/notes/1/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.java").value(2))
                .andExpect(jsonPath("$.spring").value(1));
    }

    @Test
    @DisplayName("GET /api/notes - Pagination Check")
    void getAll_ReturnsPage() throws Exception {
        when(noteService.getAllNotes(null, 0, 10)).thenReturn(Page.empty());

        mockMvc.perform(get("/api/notes")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/notes/{id} - Success")
    void getById_ValidId_ReturnsNote() throws Exception {
        String id = "123";
        NoteFullResponseDTO response = new NoteFullResponseDTO(id, "Title", "Text", null, null);

        when(noteService.getNoteById(id)).thenReturn(response);

        mockMvc.perform(get("/api/notes/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("Title"));
    }

    @Test
    @DisplayName("GET /api/notes/{id} - Not Found")
    void getById_NotFound_Returns404() throws Exception {
        String id = "999";
        String errorMessage = "Note not found with id: " + id;

        when(noteService.getNoteById(id)).thenThrow(new NoteNotFoundException(errorMessage));

        mockMvc.perform(get("/api/notes/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("PUT /api/notes/{id} - Success")
    void updateNote_ValidRequest_ReturnsUpdatedNote() throws Exception {
        String id = "123";
        NoteFullResponseDTO response = new NoteFullResponseDTO(id, "Updated Title", "Updated Text", null, null);

        when(noteService.updateNote(eq(id), any(NoteRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/notes/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Updated Title",
                                    "text": "Updated Text"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    @DisplayName("PATCH /api/notes/{id} - Success")
    void patchNote_ValidRequest_ReturnsPatchedNote() throws Exception {
        String id = "123";
        NoteFullResponseDTO response = new NoteFullResponseDTO(id, "Patched Title", "Original Text", null, null);

        when(noteService.updateNote(eq(id), any(NoteRequestDTO.class))).thenReturn(response);

        mockMvc.perform(patch("/api/notes/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Patched Title"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Patched Title"));
    }
}