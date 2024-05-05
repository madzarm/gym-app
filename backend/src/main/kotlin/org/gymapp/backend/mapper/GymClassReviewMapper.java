package org.gymapp.backend.mapper;

import org.gymapp.backend.model.GymClassReview;
import org.gymapp.library.response.GymClassReviewDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GymClassReviewMapper {

    @Mapping(target = "gymClassInstanceId", source = "gymClassInstance.id")
    @Mapping(target = "memberId", source = "member.id")
    GymClassReviewDto modelToDto(GymClassReview gymClassReview);
}
