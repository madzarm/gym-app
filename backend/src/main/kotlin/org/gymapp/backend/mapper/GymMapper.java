package org.gymapp.backend.mapper;

import org.gymapp.backend.model.Gym;
import org.gymapp.library.response.GymDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GymMapper {

    GymDto modelToDto(Gym gym);

    List<GymDto> modelsToDtos(List<Gym> gyms);

}
