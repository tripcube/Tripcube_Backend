package uos.ac.kr.repositories;

import com.querydsl.core.Tuple;
import uos.ac.kr.domains.Todo;
import uos.ac.kr.enums.TodoSortKey;

import java.util.HashMap;
import java.util.List;

public interface TodoRepositoryCustom {
    List<Todo> getTodosForUserId(int userId, TodoSortKey sortKey, int pages, int limit);

    List<Todo> getTodosForPlaceId(int placeId, String tag, TodoSortKey sortKey, int pages, int limit);

    List<Integer> getPlaceIdFromAreaCode(int areaCode1, int areaCode2, int page, String tag);
}
