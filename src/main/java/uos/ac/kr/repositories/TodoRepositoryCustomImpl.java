package uos.ac.kr.repositories;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import uos.ac.kr.domains.Todo;
import uos.ac.kr.enums.TodoSortKey;
import static uos.ac.kr.domains.QTodo.todo;

import java.util.List;

@RequiredArgsConstructor
public class TodoRepositoryCustomImpl implements TodoRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Todo> getTodos(int userId, TodoSortKey sortKey, int pages) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(todo.user.userId.eq(userId));

        OrderSpecifier<?>[] sortOrder = new OrderSpecifier[] {};

        switch (sortKey) {
            case DATE_ASC:
                sortOrder = new OrderSpecifier[] {
                        todo.createdAt.asc()
                };
                break;
            case DATE_DESC:
                sortOrder = new OrderSpecifier[] {
                        todo.createdAt.desc()
                };
                break;
            case LIKE_ASC:
                sortOrder = new OrderSpecifier[] {
                        todo.likes.asc()
                };
                break;
            case LIKE_DESC:
                sortOrder = new OrderSpecifier[] {
                        todo.likes.desc()
                };
                break;
            default:
                break;
        }

        return queryFactory.selectFrom(todo).where(builder).offset(10L *pages).limit(10).orderBy(sortOrder).fetch();

    }
}
