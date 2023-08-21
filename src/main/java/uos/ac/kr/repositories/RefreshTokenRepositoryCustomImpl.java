package uos.ac.kr.repositories;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import uos.ac.kr.domains.RefreshToken;

import java.util.Date;
import java.util.Optional;

import static uos.ac.kr.domains.QRefreshToken.refreshToken1;

@RequiredArgsConstructor
public class RefreshTokenRepositoryCustomImpl implements RefreshTokenRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<RefreshToken> getLatestOne(Integer userId) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(refreshToken1.user.userId.eq(userId));
        builder.and(refreshToken1.expiredAt.before(new Date()));

        return Optional.ofNullable(queryFactory.selectFrom(refreshToken1).orderBy(refreshToken1.createdAt.desc()).fetchFirst());
    }

    @Override
    public void logout(Integer userId) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(refreshToken1.user.userId.eq(userId));

        queryFactory.update(refreshToken1).set(refreshToken1.isExpired, "Y").where(builder).execute();
    }
}
