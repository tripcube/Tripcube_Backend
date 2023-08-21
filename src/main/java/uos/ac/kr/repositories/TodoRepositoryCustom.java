package uos.ac.kr.repositories;

import uos.ac.kr.domains.Todo;
import uos.ac.kr.enums.TodoSortKey;

import java.util.List;

public interface TodoRepositoryCustom {
    List<Todo> getTodos(int userId, TodoSortKey sortKey, int pages);
}
