package uos.ac.kr.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import uos.ac.kr.domains.User;
import uos.ac.kr.dtos.InsertUserDTO;
import uos.ac.kr.dtos.UpdateUserDTO;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User toEntity(InsertUserDTO userDTO);

    InsertUserDTO toDTO(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(UpdateUserDTO memberDTO, @MappingTarget User entity);

}
