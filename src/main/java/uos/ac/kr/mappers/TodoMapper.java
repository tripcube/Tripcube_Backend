package uos.ac.kr.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import uos.ac.kr.domains.Todo;
import uos.ac.kr.dtos.NewTodoDTO;

@Mapper(componentModel = "spring")
public interface TodoMapper {
    TodoMapper INSTANCE = Mappers.getMapper(TodoMapper.class);

    Todo toEntity(NewTodoDTO newTodoDTO);
}
