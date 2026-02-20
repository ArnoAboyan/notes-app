package com.qoqtest.notes.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Document(collection = "notes")
public class Note {
    @Id
    @EqualsAndHashCode.Include
    private String id;

    @NotBlank(message = "Title is mandatory")
    @Size(max = 255)
    private String title;

    @NotBlank(message = "Text is mandatory")
    @Size(max = 10000)
    private String text;

    private Instant createdDate = Instant.now();

    private Set<NoteTag> tags;

}
