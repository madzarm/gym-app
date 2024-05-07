package org.gymapp.backend.model

import jakarta.persistence.*
import org.gymapp.library.response.CriteriaType
import java.util.UUID


@Entity
class ChallengeCriteria (
    @Id
    val id: String = UUID.randomUUID().toString(),

    @Enumerated(EnumType.STRING)
    var type: CriteriaType,

    @OneToOne
    var challenge: Challenge? = null,
)