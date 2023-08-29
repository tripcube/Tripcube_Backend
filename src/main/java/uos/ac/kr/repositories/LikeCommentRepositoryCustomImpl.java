package uos.ac.kr.repositories;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import uos.ac.kr.domains.Like_Comment;
import static uos.ac.kr.domains.QLike_Comment.like_Comment;
import static uos.ac.kr.domains.QTodo.todo;
import static uos.ac.kr.domains.QUser.user;

import java.util.List;

@RequiredArgsConstructor
public class LikeCommentRepositoryCustomImpl implements LikeCommentRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Like_Comment> getLikeCommentsFromCommentID(int commentId) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(like_Comment.likeCommentId.commentId.eq(commentId));

        return queryFactory.selectFrom(like_Comment).where(builder).fetch();
    }
}
