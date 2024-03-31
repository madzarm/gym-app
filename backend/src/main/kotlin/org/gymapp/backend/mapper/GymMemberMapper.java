package org.gymapp.backend.mapper;

import org.gymapp.backend.model.GymMember;
import org.gymapp.library.response.GymMemberDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {GymMapper.class, UserMapper.class})
public interface GymMemberMapper {

    GymMemberDto modelToDto(GymMember gymMember);

    List<GymMemberDto> modelsToDtos(List<GymMember> gymMembers);

}
