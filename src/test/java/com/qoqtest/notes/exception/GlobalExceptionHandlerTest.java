package com.qoqtest.notes.exception;

import com.qoqtest.notes.controller.NoteController;
import com.qoqtest.notes.service.NoteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NoteController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NoteService noteService;

    @Test
    @DisplayName("Should return 500 when unexpected exception occurs")
    void handleGeneralException_Returns500() throws Exception {
        when(noteService.getNoteById(anyString())).thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/api/notes/any-id"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("An unexpected error occurred"))
                .andExpect(jsonPath("$.status").value(500));
    }

    @Test
    @DisplayName("Should return 400 when validation fails")
    void handleValidationErrors_ReturnsFormattedErrors() throws Exception {
        mockMvc.perform(post("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"\", \"text\": \"valid text\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.title").exists())
                .andExpect(jsonPath("$.validationErrors.title").value("Title is mandatory"));
    }
}
