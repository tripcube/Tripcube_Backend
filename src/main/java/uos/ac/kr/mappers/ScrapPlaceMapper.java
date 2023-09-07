package uos.ac.kr.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import uos.ac.kr.domains.Scrap_Place;
import uos.ac.kr.dtos.GetScrapPlaceDTO;

@Mapper(componentModel = "spring")
public interface ScrapPlaceMapper {
    ScrapPlaceMapper INSTANCE = Mappers.getMapper(ScrapPlaceMapper.class);

    GetScrapPlaceDTO toDTO(Scrap_Place scrapPlace);
}
