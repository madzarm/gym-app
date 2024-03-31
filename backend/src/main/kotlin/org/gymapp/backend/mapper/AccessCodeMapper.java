package org.gymapp.backend.mapper;

import org.gymapp.backend.model.AccessCode;
import org.gymapp.library.response.AccessCodeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccessCodeMapper {

    @Mapping(source = "gym.id", target = "gymId")
    AccessCodeDto modelToDto(AccessCode accessCode);

}
