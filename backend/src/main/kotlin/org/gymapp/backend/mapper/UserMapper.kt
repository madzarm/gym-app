package org.gymapp.backend.mapper

import org.gymapp.backend.model.GymUser
import org.gymapp.backend.model.User
import org.gymapp.library.request.CreateUserRequest
import org.gymapp.library.response.UserDto
import org.mapstruct.*
import org.mapstruct.factory.Mappers

@Mapper(componentModel = "spring")
interface UserMapper {

    companion object {
        val INSTANCE: UserMapper = Mappers.getMapper(UserMapper::class.java)
    }

    @Mappings(
        Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())"),
        Mapping(target = "updatedAt", ignore = true),
        Mapping(target = "gymUsers", ignore = true)
    )
    fun requestToUser(createUserRequest: CreateUserRequest): User

    @Mapping(source = "gymUsers", target = "gymUsersIds")
    fun modelToDto(user: User): UserDto

    fun modelsToDtos(users: List<User>): List<UserDto>

    fun mapGymUserToId(gymUser: GymUser): String {
        return gymUser.id
    }
}