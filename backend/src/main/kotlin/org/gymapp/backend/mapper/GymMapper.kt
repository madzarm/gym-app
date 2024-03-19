package org.gymapp.backend.mapper

import org.gymapp.backend.model.Gym
import org.gymapp.library.response.GymDto
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface GymMapper {

    fun modelToDto(gym: Gym?): GymDto

    fun modelsToDtos(gyms: List<Gym?>): List<GymDto>
}