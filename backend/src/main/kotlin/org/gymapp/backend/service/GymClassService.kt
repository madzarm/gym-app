package org.gymapp.backend.service

import org.gymapp.backend.mapper.GymClassMapper
import org.gymapp.backend.model.GymClass
import org.gymapp.backend.repository.GymClassRepository
import org.gymapp.library.response.GymClassDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GymClassService(
    @Autowired private val gymClassRepository: GymClassRepository,
    @Autowired private val gymClassMapper: GymClassMapper,
) {

    fun findById(classId: String): GymClass {
        return gymClassRepository.findById(classId)
            .orElseThrow { IllegalArgumentException("Class not found") }
    }

    fun findByIdDto(classId: String): GymClassDto {
        val gymCLass = gymClassRepository.findById(classId)
            .orElseThrow { IllegalArgumentException("Class not found") }

        return gymClassMapper.modelToDto(gymCLass)
    }
}