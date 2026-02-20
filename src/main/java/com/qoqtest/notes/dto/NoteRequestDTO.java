package com.qoqtest.notes.dto;

import com.qoqtest.notes.entity.NoteTag;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;

public record NoteRequestDTO(
        @NotBlank(message = "Title is mandatory")
        @Schema(description = "Note title", example = "Shopping List")
        String title,

        @NotBlank(message = "Text is mandatory")
        @Schema(description = "Main content of the note", example = "Buy milk, eggs, and bread")
        String text,

        @Schema(description = "Set of tags for categorization", example = "[\"PERSONAL\", \"IMPORTANT\"]")
        Set<NoteTag> tags
) {}
