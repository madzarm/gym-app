package org.gymapp.backend.repository

import org.gymapp.backend.model.AccessCode
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AccessCodeRepository: JpaRepository<AccessCode, String> {
    fun findByCode(code: String): AccessCode?
    fun findByGymId(gymId: String): List<AccessCode>
    fun deleteByCode(code: String)
    fun deleteByGymId(gymId: String)

}