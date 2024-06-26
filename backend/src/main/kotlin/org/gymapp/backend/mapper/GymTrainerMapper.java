package org.gymapp.backend.mapper;

import org.gymapp.backend.model.GymTrainer;
import org.gymapp.library.response.GymTrainerDto;
import org.gymapp.library.response.GymTrainerWithReviewsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {GymMapper.class, UserMapper.class, GymClassMapper.class, GymTrainerReviewMapper.class})
public interface GymTrainerMapper {

    @Mapping(target = "gymClasses", source = "classes")
    GymTrainerDto modelToDto(GymTrainer gymOwner);

    List<GymTrainerDto> modelsToDtos(List<GymTrainer> gymOwners);

    @Mapping(target = "user", source = "gymUser.user")
    GymTrainerWithReviewsDto modelToDtoWithReviews(GymTrainer gymOwner);

    List<GymTrainerWithReviewsDto> modelsToDtosWithReviews(List<GymTrainer> gymOwners);

}
