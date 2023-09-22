package uos.ac.kr.repositories;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
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
    public List<Todo> getTodosForPlaceId(int placeId, int tag, TodoSortKey sortKey, int pages, int limit) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(todo.placeId.eq(placeId));
        if (tag != 0) {
            builder.and(todo.tag.eq(tag));
        }

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

    @Override
    public List<Integer> getPlaceIdFromAreaCode(int areaCode1, int areaCode2, int page, int tag) {
        BooleanBuilder builder = new BooleanBuilder();
        if (areaCode1 != 0) {
            builder.and(todo.areaCode1.eq(areaCode1));
        }
        if (areaCode2 != 0) {
            builder.and(todo.areaCode2.eq(areaCode2));
        }
        builder.and(todo.tag.eq(tag));

        return queryFactory.select(todo.placeId).from(todo).groupBy(todo.placeId).where(builder).orderBy(todo.likes.sum().desc()).offset((page-1)*5).limit(5).fetch();
    }
}
