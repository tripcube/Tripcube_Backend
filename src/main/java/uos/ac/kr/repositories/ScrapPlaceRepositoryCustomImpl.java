package uos.ac.kr.repositories;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import uos.ac.kr.domains.Scrap_Place;
import uos.ac.kr.domains.User;

import java.util.Optional;

import static uos.ac.kr.domains.QUser.user;
import static uos.ac.kr.domains.QScrap_Place.scrap_Place;

@RequiredArgsConstructor
public class ScrapPlaceRepositoryCustomImpl implements ScrapPlaceRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Scrap_Place> getDuplicateOne(int userId, int placeId) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(scrap_Place.user.userId.eq(userId));
        builder.and(scrap_Place.placeId.eq(placeId));

        return Optional.ofNullable(queryFactory.selectFrom(scrap_Place).where(builder).fetchFirst());
    }
}
