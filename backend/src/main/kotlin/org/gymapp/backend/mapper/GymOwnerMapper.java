package org.gymapp.backend.mapper;

import org.gymapp.backend.model.GymOwner;
import org.gymapp.library.response.GymOwnerDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {GymMapper.class, UserMapper.class})
public interface GymOwnerMapper {
    GymOwnerDto modelToDto(GymOwner gymOwner);

    List<GymOwnerDto> modelsToDtos(List<GymOwner> gymOwners);

}
