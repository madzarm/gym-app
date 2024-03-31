package org.gymapp.backend.mapper;

import org.gymapp.backend.model.GymClass;
import org.gymapp.library.response.GymClassDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {GymMemberMapper.class})
public interface GymClassMapper {

    @Mapping(target = "duration", source = "duration", dateFormat = "HH:mm")
    GymClassDto modelToDto(GymClass gymClass);

    List<GymClassDto> modelsToDtos(List<GymClass> gymClasses);
}
