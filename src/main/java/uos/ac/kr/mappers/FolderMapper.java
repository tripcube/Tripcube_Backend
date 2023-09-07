package uos.ac.kr.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import uos.ac.kr.domains.Folder;
import uos.ac.kr.dtos.GetFolderDTO;

@Mapper(componentModel = "spring")
public interface FolderMapper {
    FolderMapper INSTANCE = Mappers.getMapper(FolderMapper.class);

    GetFolderDTO toDTO(Folder folder);
}
