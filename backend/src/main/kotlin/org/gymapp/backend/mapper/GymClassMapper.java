package org.gymapp.backend.mapper;

import org.gymapp.backend.model.GymClass;
import org.gymapp.backend.model.GymClassReview;
import org.gymapp.backend.model.RecurringPattern;
import org.gymapp.library.response.GymClassDto;
import org.gymapp.library.response.GymClassWithReviewsDto;
import org.gymapp.library.response.RecurringPatternDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {GymClassInstanceMapper.class, GymClassReviewMapper.class, GymClassMapper.class})
public interface GymClassMapper {

    @Mapping(target = "duration", source = "duration", qualifiedByName = "durationToString")
    @Mapping(target = "trainerId", source = "trainer.id")
    @Mapping(target = "isRecurring", source = "recurring")
    @Mapping(target = "isDeleted", source = "deleted")
    GymClassDto modelToDto(GymClass gymClass);

    List<GymClassDto> modelsToDtos(List<GymClass> gymClasses);

    RecurringPatternDto modelToDto(RecurringPattern recurringPattern);

    @Mapping(target = "reviews", source = "reviews")
    @Mapping(target = "gymClass", source = "gymClass")
    GymClassWithReviewsDto modelToDtoWithReviews(GymClass gymClass, List<GymClassReview> reviews);

}
