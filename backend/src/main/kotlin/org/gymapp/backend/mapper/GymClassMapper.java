package org.gymapp.backend.mapper;

import org.gymapp.backend.model.GymClass;
import org.gymapp.backend.model.GymMember;
import org.gymapp.library.response.GymClassDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Duration;
import java.util.List;

@Mapper(componentModel = "spring")
public interface GymClassMapper {

    @Mapping(target = "duration", source = "duration", qualifiedByName = "durationToString")
    @Mapping(target = "participantsIds", source = "participants", qualifiedByName = "participantsToIds")
    GymClassDto modelToDto(GymClass gymClass);

    List<GymClassDto> modelsToDtos(List<GymClass> gymClasses);

    @Named("participantsToIds")
    default List<String> participantsToIds(List<GymMember> participants) {
        return participants.stream().map(GymMember::getId).toList();
    }

    @Named("durationToString")
    default String durationToString(Duration duration) {
        return String.valueOf(duration.toMinutes());
    }
}
