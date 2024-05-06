package org.gymapp.backend.mapper;

import org.gymapp.backend.model.GymMember;
import org.gymapp.backend.model.GymMemberDtoFull;
import org.gymapp.library.response.GymMemberDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {GymMapper.class, UserMapper.class, GymClassMapper.class, GymVisitMapper.class, GymClassInstanceMapper.class})
public interface GymMemberMapper {

    @Mapping(target = "gymClasses", source = "classes")
    @Mapping(target = "firstName", source = "gymUser.user.firstName")
    @Mapping(target = "lastName", source = "gymUser.user.lastName")
    GymMemberDto modelToDto(GymMember gymMember);


    GymMemberDtoFull modelToDtoFull(GymMember gymMember);

    List<GymMemberDto> modelsToDtos(List<GymMember> gymMembers);

}
