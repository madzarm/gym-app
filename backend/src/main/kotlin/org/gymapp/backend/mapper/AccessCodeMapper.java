package org.gymapp.backend.mapper;

import org.gymapp.backend.model.AccessCode;
import org.gymapp.backend.model.AccessCodeDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccessCodeMapper {

    AccessCodeDto modelToDto(AccessCode accessCode);

}
