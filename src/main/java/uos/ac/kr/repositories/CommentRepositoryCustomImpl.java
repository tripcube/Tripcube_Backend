package uos.ac.kr.repositories;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import uos.ac.kr.domains.Comment;
import uos.ac.kr.enums.CommentSortKey;

import java.util.List;

import static uos.ac.kr.domains.QComment.comment;
import static uos.ac.kr.domains.QTodo.todo;

@RequiredArgsConstructor
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Comment> getCommentsFromUserId(int userId, CommentSortKey sortKey, int pages) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(comment.user.userId.eq(userId));

        OrderSpecifier<?>[] sortOrder = new OrderSpecifier[] {};

        switch (sortKey) {
            case DATE_ASC:
                sortOrder = new OrderSpecifier[] {
                        comment.createdAt.asc()
                };
                break;
            case DATE_DESC:
                sortOrder = new OrderSpecifier[] {
                        comment.createdAt.desc()
                };
                break;
            case LIKE_ASC:
                sortOrder = new OrderSpecifier[] {
                        comment.likes.asc()
                };
                break;
            case LIKE_DESC:
                sortOrder = new OrderSpecifier[] {
                        comment.likes.desc()
                };
                break;
            default:
                break;
        }

        return queryFactory.selectFrom(comment).join(comment.todo, todo).where(builder).offset(10L *pages).limit(10).orderBy(sortOrder).fetch();

    }

    @Override
    public List<Comment> getCommentsFromTodoID(int todoId, CommentSortKey sortKey, int pages) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(comment.todo.todoId.eq(todoId));

        OrderSpecifier<?>[] sortOrder = new OrderSpecifier[] {};

        switch (sortKey) {
            case DATE_ASC:
                sortOrder = new OrderSpecifier[] {
                        comment.createdAt.asc()
                };
                break;
            case DATE_DESC:
                sortOrder = new OrderSpecifier[] {
                        comment.createdAt.desc()
                };
                break;
            case LIKE_ASC:
                sortOrder = new OrderSpecifier[] {
                        comment.likes.asc()
                };
                break;
            case LIKE_DESC:
                sortOrder = new OrderSpecifier[] {
                        comment.likes.desc()
                };
                break;
            default:
                break;
        }

        return queryFactory.selectFrom(comment).join(comment.todo, todo).where(builder).offset(10L *pages).limit(10).orderBy(sortOrder).fetch();

    }
}
