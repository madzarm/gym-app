package org.gymapp.backend.repository

import org.gymapp.backend.model.GymMember
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GymMemberRepository: JpaRepository<GymMember, String> {

    fun findByInviteCode(inviteCode: String): GymMember?

}