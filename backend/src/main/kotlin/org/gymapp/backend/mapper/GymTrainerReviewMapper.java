package org.gymapp.backend.mapper;

import org.gymapp.backend.model.GymTrainerReview;
import org.gymapp.library.response.GymTrainerReviewDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GymTrainerReviewMapper {

    @Mapping(target = "trainerId", source = "trainer.id")
    @Mapping(target = "memberId", source = "member.id")
    GymTrainerReviewDto modelToDto(GymTrainerReview review);
}
