package uos.ac.kr.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import uos.ac.kr.domains.Comment;
import uos.ac.kr.dtos.GetCommentDTO;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    GetCommentDTO toDTO(Comment comment);
}
