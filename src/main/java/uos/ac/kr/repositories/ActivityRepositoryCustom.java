package uos.ac.kr.repositories;

import uos.ac.kr.domains.Activity;

import java.util.List;

public interface ActivityRepositoryCustom {
    List<Integer> getHotActivity(int areaCode1, int areaCode2, int page);

    Activity getOneActivity(int placeId);
}
