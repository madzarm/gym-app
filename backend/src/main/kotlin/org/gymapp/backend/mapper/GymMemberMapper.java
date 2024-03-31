package org.gymapp.backend.mapper;

import org.gymapp.backend.model.GymMember;
import org.gymapp.backend.model.GymMemberDto;
import org.gymapp.backend.model.GymOwner;
import org.gymapp.backend.model.GymOwnerDto;
import org.gymapp.backend.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {GymMapper.class, UserMapper.class})
public interface GymMemberMapper {

    GymMemberDto modelToDto(GymMember gymMember);

    List<GymMemberDto> modelsToDtos(List<GymMember> gymMembers);

}
