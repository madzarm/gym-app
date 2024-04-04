package org.gymapp.backend.mapper;

import org.gymapp.backend.model.GymClassReview;
import org.gymapp.backend.model.GymClassReviewDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GymClassReviewMapper {

    @Mapping(target = "gymClassId", source = "gymClass.id")
    @Mapping(target = "memberId", source = "member.id")
    GymClassReviewDto modelToDto(GymClassReview gymClassReview);
}
