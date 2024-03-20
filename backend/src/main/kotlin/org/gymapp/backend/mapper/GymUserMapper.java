package org.gymapp.backend.mapper;

import org.gymapp.backend.model.GymUser;
import org.gymapp.backend.model.Role;
import org.gymapp.library.response.GymUserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = {GymMapper.class, UserMapper.class})
public interface GymUserMapper {

    @Mapping(source = "roles", target = "roles", qualifiedByName = "mapRolesToNames")
    GymUserDto modelToDto(GymUser gymUser);

    List<GymUserDto> modelsToDtos(List<GymUser> gymUsers);

    @Named("mapRolesToNames")
    default List<String> mapRolesToNames(List<Role> roles) {
        return roles.stream().map(this::mapRoleToName).toList();
    }

    default String mapRoleToName(Role role) {
        return role.getName();
    }
}
