package org.gymapp.backend.mapper;

import org.gymapp.backend.model.GymClassModifiedInstance;
import org.gymapp.library.response.GymClassModifiedInstanceDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Duration;

@Mapper(componentModel = "spring")
public interface GymClassModifiedInstanceMapper {

    @Mapping(target = "trainerId", source = "trainer.id")
    @Mapping(target = "isCanceled", source = "canceled")
    GymClassModifiedInstanceDto modelToDto(GymClassModifiedInstance gymClassInstance);

    default String durationToString(Duration duration) {
        return String.valueOf(duration.toMinutes());
    }
}
