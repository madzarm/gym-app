package org.gymapp.backend.mapper;

import org.gymapp.backend.model.ChallengeCriteria;
import org.gymapp.backend.model.CriteriaType;
import org.gymapp.backend.repository.FrequencyBasedCriteriaRepository;
import org.gymapp.backend.repository.TimeBasedCriteriaRepository;
import org.gymapp.library.response.CriteriaDto;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class ChallengeCriteriaMapper {

    @Autowired
    private FrequencyBasedCriteriaRepository frequencyBasedCriteriaRepository;

    @Autowired
    private TimeBasedCriteriaRepository timeBasedCriteriaRepository;

     CriteriaDto modelToDto(ChallengeCriteria challengeCriteria) {
        var type = challengeCriteria.getType();
        CriteriaDto criteriaDto = new CriteriaDto(challengeCriteria.getId(), type.name(), null, null, null);
        if (CriteriaType.TIMED_VISIT_BASED.equals(type)) {
            var timeBasedCriteria = timeBasedCriteriaRepository.findByBaseCriteriaId(challengeCriteria.getId()).orElseThrow();
            criteriaDto.setStartTimeCriteria(String.valueOf(timeBasedCriteria.getStartTime()));
            criteriaDto.setEndTimeCriteria(String.valueOf(timeBasedCriteria.getEndTime()));
        } else if (CriteriaType.FREQUENCY_BASED.equals(type)) {
            var freqBasedCriteria = frequencyBasedCriteriaRepository.findByBaseCriteriaId(challengeCriteria.getId()).orElseThrow();
            criteriaDto.setFrequencyCount(freqBasedCriteria.getFrequencyCount());
        }
        return criteriaDto;
    }
}
