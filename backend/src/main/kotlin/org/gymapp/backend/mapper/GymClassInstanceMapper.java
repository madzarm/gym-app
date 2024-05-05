package org.gymapp.backend.mapper;

import org.gymapp.backend.model.DayOfWeek;
import org.gymapp.backend.model.GymClass;
import org.gymapp.backend.model.GymClassInstance;
import org.gymapp.backend.model.GymClassModifiedInstance;
import org.gymapp.backend.model.GymMember;
import org.gymapp.backend.repository.GymClassModifiedInstanceRepository;
import org.gymapp.library.response.GymClassDto;
import org.gymapp.library.response.GymClassInstanceDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring", uses = {GymClassModifiedInstanceMapper.class})
public interface GymClassInstanceMapper {

    @Mapping(target = "duration", source = "gymClass.duration", qualifiedByName = "durationToString")
    @Mapping(target = "participantsIds", source = "participants", qualifiedByName = "participantsToIds")
    @Mapping(target = "trainerId", source = "gymClass.trainer.id")
    @Mapping(target = "name", source = "gymClass.name")
    @Mapping(target = "description", source = "gymClass.description")
    @Mapping(target = "maxParticipants", source = "gymClass.maxParticipants")
    @Mapping(target = "classId", source = "gymClass.id")
    GymClassInstanceDto modelToDto(GymClassInstance gymClassInstance);

    List<GymClassDto> modelsToDtos(List<GymClass> gymClasses);

    @Named("participantsToIds")
    default List<String> participantsToIds(List<GymMember> participants) {
        return participants.stream().map(GymMember::getId).toList();
    }

    @Named("durationToString")
    default String durationToString(Duration duration) {
        return String.valueOf(duration.toMinutes());
    }

    default List<Integer> dayOfWeeksToInt(List<DayOfWeek> dayOfWeeks) {
        return dayOfWeeks.stream().map(DayOfWeek::getDayOfWeek).toList();
    }

}
