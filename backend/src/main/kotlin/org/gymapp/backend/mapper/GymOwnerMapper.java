package org.gymapp.backend.mapper;

import org.gymapp.backend.model.GymOwner;
import org.gymapp.backend.model.GymOwnerDto;
import org.gymapp.backend.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {GymMapper.class, UserMapper.class})
public interface GymOwnerMapper {
    GymOwnerDto modelToDto(GymOwner gymOwner);

    List<GymOwnerDto> modelsToDtos(List<GymOwner> gymOwners);

}
