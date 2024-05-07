package org.gymapp.backend.repository;

import org.gymapp.backend.model.MemberChallenge
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberChallengeRepository : JpaRepository<MemberChallenge, String> {

    fun findByMemberId(memberId: String): List<MemberChallenge>
}