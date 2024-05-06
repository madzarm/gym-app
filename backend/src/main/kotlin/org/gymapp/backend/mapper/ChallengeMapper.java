package org.gymapp.backend.mapper;

import org.gymapp.backend.model.Challenge;
import org.gymapp.backend.model.ChallengeDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ChallengeMapper {

    @Autowired
    private ChallengeCriteriaMapper criteriaMapper;

    @Mapping(target = "gymId", source = "gym.id")
    @Mapping(target = "isDeleted", source = "deleted")
    public abstract ChallengeDto modelToDto(Challenge challenge);

    @AfterMapping
    protected void setCriteria(Challenge challenge, @MappingTarget ChallengeDto challengeDto) {
        var criteriaDto = criteriaMapper.modelToDto(challenge.getCriteria());
        challengeDto.setCriteriaDto(criteriaDto);
    }

    public abstract List<ChallengeDto> modelsToDtos(List<Challenge> challenges);
}
