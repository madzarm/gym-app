package org.gymapp.backend.mapper;

import org.gymapp.backend.model.GymTrainer;
import org.gymapp.library.response.GymTrainerDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {GymMapper.class, UserMapper.class})
public interface GymTrainerMapper {
    GymTrainerDto modelToDto(GymTrainer gymOwner);

    List<GymTrainerDto> modelsToDtos(List<GymTrainer> gymOwners);

}
