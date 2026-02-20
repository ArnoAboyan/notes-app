package com.qoqtest.notes.mapper;

import com.qoqtest.notes.dto.NoteFullResponseDTO;
import com.qoqtest.notes.dto.NoteRequestDTO;
import com.qoqtest.notes.dto.NoteShortResponseDTO;
import com.qoqtest.notes.entity.Note;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface NoteMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    Note toEntity(NoteRequestDTO dto);

    NoteFullResponseDTO toFullResponseDTO(Note note);

    NoteShortResponseDTO toShortResponseDTO(Note note);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(NoteRequestDTO dto, @MappingTarget Note note);

}
