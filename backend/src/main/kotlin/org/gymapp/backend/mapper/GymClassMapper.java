package org.gymapp.backend.mapper;

import org.gymapp.backend.model.DayOfWeek;
import org.gymapp.backend.model.GymClass;
import org.gymapp.backend.model.GymMember;
import org.gymapp.backend.model.RecurringPattern;
import org.gymapp.library.response.GymClassDto;
import org.gymapp.library.response.RecurringPatternDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Duration;
import java.util.List;

@Mapper(componentModel = "spring", uses = {GymClassInstanceMapper.class})
public interface GymClassMapper {

    @Mapping(target = "duration", source = "duration", qualifiedByName = "durationToString")
    @Mapping(target = "trainerId", source = "trainer.id")
    GymClassDto modelToDto(GymClass gymClass);

    List<GymClassDto> modelsToDtos(List<GymClass> gymClasses);

    RecurringPatternDto modelToDto(RecurringPattern recurringPattern);

}
