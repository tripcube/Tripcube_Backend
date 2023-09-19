package uos.ac.kr.repositories;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import uos.ac.kr.domains.Activity;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static uos.ac.kr.domains.QActivity.activity;

@RequiredArgsConstructor
public class ActivityRepositoryCustomImpl implements ActivityRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Integer> getHotActivity(int areaCode1, int areaCode2, int page) {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, -1);

        BooleanBuilder builder = new BooleanBuilder();
        if (areaCode1 != 0) {
            builder.and(activity.areaCode1.eq(areaCode1));
        }
        if (areaCode2 != 0) {
            builder.and(activity.areaCode2.eq(areaCode2));
        }
        builder.and(activity.createdAt.after(cal.getTime()));

        return queryFactory.select(activity.placeId).from(activity).where(builder).groupBy(activity.placeId).orderBy(activity.placeId.count().desc()).offset((page-1)*5).limit(5).fetch();

    }
    @Override
    public Activity getOneActivity(int placeId) {
        return queryFactory.selectFrom(activity).where(activity.placeId.eq(placeId)).limit(1).fetchOne();
    }
}
