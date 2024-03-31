package org.gymapp.backend.mapper;

import org.gymapp.backend.model.GymUser;
import org.gymapp.backend.model.User;
import org.gymapp.library.request.CreateUserRequest;
import org.gymapp.library.response.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "gymUsers", ignore = true)
    User requestToUser(CreateUserRequest createUserRequest);

    @Mapping(source = "gymUsers", target = "gymUsersIds")
    UserDto modelToDto(User user);

    List<UserDto> modelsToDtos(List<User> user); 

    default String mapGymUserToId(GymUser gymUser) {
        return gymUser.getId();
    }
}
