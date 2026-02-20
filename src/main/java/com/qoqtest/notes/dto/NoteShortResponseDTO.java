package com.qoqtest.notes.dto;

import java.time.Instant;

public record NoteShortResponseDTO(
        String id,
        String title,
        Instant createdDate
) {}
