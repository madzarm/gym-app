package org.gymapp.backend.model

import org.gymapp.library.response.GymClassDto
import org.gymapp.library.response.UserDto

data class GymMemberDtoFull (
    val user: UserDto,
    val classes: List<GymClassDto> = mutableListOf(),
    val firstJoined: String,
    val visits: List<GymVisitDto> = mutableListOf()
)