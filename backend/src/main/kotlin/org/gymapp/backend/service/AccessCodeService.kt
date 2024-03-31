package org.gymapp.backend.service

import org.gymapp.backend.common.Common
import org.gymapp.backend.mapper.AccessCodeMapper
import org.gymapp.backend.model.AccessCode
import org.gymapp.backend.model.Gym
import org.gymapp.backend.repository.AccessCodeRepository
import org.gymapp.library.response.AccessCodeDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID

@Service
class AccessCodeService (
    @Autowired private var accessCodeRepository: AccessCodeRepository,
    @Autowired private var common: Common,
    @Autowired private var accessCodeMapper: AccessCodeMapper,
) {

    fun generateAccessCodeDto(gym: Gym): AccessCodeDto {
        val accessCode = AccessCode(
            UUID.randomUUID().toString(),
            common.generateRandomCode(),
            LocalDateTime.now().plus(15, ChronoUnit.MINUTES),
            gym
        )
        accessCodeRepository.save(accessCode)
        return accessCodeMapper.modelToDto(accessCode)
    }

    fun findAccessCodeByCode(code: String): AccessCode {
        return accessCodeRepository.findByCode(code) ?: throw IllegalArgumentException("Access code not found!")
    }

    fun deleteAccessCode(accessCode: AccessCode) {
        accessCodeRepository.delete(accessCode)
    }
}