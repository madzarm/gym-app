package org.gymapp.backend.mapper

import org.gymapp.backend.model.GymUser
import org.gymapp.backend.model.User
import org.gymapp.library.request.CreateUserRequest
import org.gymapp.library.response.UserDto
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import org.mapstruct.factory.Mappers

@Mapper(componentModel = "spring")
interface UserMapper {

    companion object {
        val INSTANCE: UserMapper = Mappers.getMapper(UserMapper::class.java)
    }

    @Mappings(
        // Assuming direct mapping for most fields, only need to specify special cases
        Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())"),
        Mapping(target = "updatedAt", ignore = true),
        Mapping(target = "gymUsers", ignore = true)
    )
    fun createUserRequestToUser(createUserRequest: CreateUserRequest): User

    @Mapping(source = "gymUsers", target = "gymUserIds")
    fun userToUserDto(user: User): UserDto

    fun usersToUserDtos(users: List<User>): List<UserDto>

    // MapStruct will use this method for each element in the collection
    fun mapGymUserToId(gymUser: GymUser): String {
        return gymUser.id
    }
}