package uos.ac.kr.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uos.ac.kr.domains.Scrap_Place;

public interface ScrapPlaceRepository extends JpaRepository<Scrap_Place, Integer>, ScrapPlaceRepositoryCustom{
}
