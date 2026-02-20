package com.qoqtest.notes.dto;

import com.qoqtest.notes.entity.NoteTag;
import java.time.Instant;
import java.util.Set;

public record NoteFullResponseDTO(
        String id,
        String title,
        String text,
        Instant createdDate,
        Set<NoteTag> tags
) {}
