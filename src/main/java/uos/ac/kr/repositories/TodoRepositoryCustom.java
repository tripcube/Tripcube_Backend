package uos.ac.kr.repositories;

import uos.ac.kr.domains.Todo;
import uos.ac.kr.enums.TodoSortKey;

import java.util.List;

public interface TodoRepositoryCustom {
    List<Todo> getTodosForUserId(int userId, TodoSortKey sortKey, int pages, int limit);

    List<Todo> getTodosForPlaceId(int placeId, TodoSortKey sortKey, int pages, int limit);
}
