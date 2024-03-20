package org.gymapp.backend.service

import org.gymapp.backend.repository.RoleRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class RoleService(
    @Autowired private val roleRepository: RoleRepository
) {

    fun findByName(name: String) = roleRepository.findByName(name)
}