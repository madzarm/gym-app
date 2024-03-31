package org.gymapp.backend.controller

import org.gymapp.backend.common.Common
import org.gymapp.backend.service.TrainerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/trainers")
class TrainerController (
    @Autowired private val trainerService: TrainerService,
    @Autowired private val common: Common
){


}