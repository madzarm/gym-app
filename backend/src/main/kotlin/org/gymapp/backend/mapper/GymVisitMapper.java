package org.gymapp.backend.mapper;

import org.gymapp.backend.model.GymVisit;
import org.gymapp.library.response.GymVisitDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Duration;
import java.util.List;

@Mapper(componentModel = "spring")
public interface GymVisitMapper {

    @Mapping(target = "gymId", source = "gym.id")
    @Mapping(target = "gymMemberId", source = "gymMember.id")
    @Mapping(target = "duration", source = "duration", qualifiedByName = "durationToMinutes")
    GymVisitDto modelToDto(GymVisit gymVisit);

    List<GymVisitDto> modelsToDtos(List<GymVisit> gymVisits);

    @Named("durationToMinutes")
    default String durationToMinutes(Duration duration) {
        if (duration == null) {
            return null;
        }
        return String.valueOf(duration.toMinutes());
    }
}
