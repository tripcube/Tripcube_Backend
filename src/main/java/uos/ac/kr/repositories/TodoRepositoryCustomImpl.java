package uos.ac.kr.repositories;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import uos.ac.kr.domains.Todo;
import uos.ac.kr.enums.TodoSortKey;
import static uos.ac.kr.domains.QTodo.todo;
import static uos.ac.kr.domains.QUser.user;

import java.util.List;

@RequiredArgsConstructor
public class TodoRepositoryCustomImpl implements TodoRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Todo> getTodosForUserId(int userId, TodoSortKey sortKey, int pages, int limit) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(user.userId.eq(userId));

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

        return queryFactory.selectFrom(todo).join(todo.user, user).where(builder).offset(10L *pages).limit(limit).orderBy(sortOrder).fetch();

    }

    @Override
    public List<Todo> getTodosForPlaceId(int placeId, TodoSortKey sortKey, int pages, int limit) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(todo.placeId.eq(placeId));

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

        return queryFactory.selectFrom(todo).where(builder).offset(limit * pages).orderBy(sortOrder).limit(limit).fetch();

    }
}
