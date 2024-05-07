package org.gymapp.backend.repository;

import org.gymapp.backend.model.MemberChallenge
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MemberChallengeRepository : JpaRepository<MemberChallenge, String> {

    fun findByMemberId(memberId: String): List<MemberChallenge>
    fun findByMemberIdAndChallengeId(memberId: String, challengeId: String): Optional<MemberChallenge>
}