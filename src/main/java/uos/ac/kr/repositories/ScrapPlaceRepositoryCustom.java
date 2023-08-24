package uos.ac.kr.repositories;

import uos.ac.kr.domains.Scrap_Place;

import java.util.Optional;

public interface ScrapPlaceRepositoryCustom {
    public Optional<Scrap_Place> getDuplicateOne(int userId, int placeId);
}
