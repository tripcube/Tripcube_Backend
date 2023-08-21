package uos.ac.kr.repositories;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import uos.ac.kr.domains.User;
import uos.ac.kr.dtos.SelectUserDTO;
import uos.ac.kr.enums.UserSortKey;

import java.util.List;
import java.util.Optional;

import static uos.ac.kr.domains.QUser.user;

@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<User> getDuplicateOne(String id, String name) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.or(user.loginId.eq(id));
        builder.or(user.name.eq(name));

        //Member newMember = queryFactory.selectFrom(member).where(builder).fetchFirst();
        return Optional.ofNullable(queryFactory.selectFrom(user).where(builder).fetchFirst());
    }

    @Override
    public Optional<User> getUserById(String id) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(user.loginId.eq(id));

        //Member newMember = queryFactory.selectFrom(member).where(builder).fetchFirst();
        return Optional.ofNullable(queryFactory.selectFrom(user).where(builder).fetchFirst());
    }

    @Override
    public Optional<User> getUserByName(String name) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(user.name.eq(name));

        //Member newMember = queryFactory.selectFrom(member).where(builder).fetchFirst();
        return Optional.ofNullable(queryFactory.selectFrom(user).where(builder).fetchFirst());
    }

    @Override
    public List<User> getUsers(SelectUserDTO selectDTO) {
        BooleanBuilder builder = new BooleanBuilder();

        String name = selectDTO.getNickName();
        builder.or(StringUtils.isNullOrEmpty(name) ? null : user.name.like("%" + name +"%"));

        String id = selectDTO.getId();
        builder.or(StringUtils.isNullOrEmpty(id) ? null : user.loginId.like("%" + id +"%"));

        UserSortKey sortKey = selectDTO.getSortKey();

        OrderSpecifier<?>[] sortOrder = new OrderSpecifier[] {};

        switch (sortKey) {
            case ID_ASC:
                sortOrder = new OrderSpecifier[] {
                        user.loginId.asc(),
                        user.userId.asc()
                };
                break;
            case ID_DESC:
                sortOrder = new OrderSpecifier[] {
                        user.loginId.desc(),
                        user.userId.desc()
                };
                break;
            case NAME_ASC:
                sortOrder = new OrderSpecifier[] {
                        user.name.asc(),
                        user.userId.asc()
                };
                break;
            case NAME_DESC:
                sortOrder = new OrderSpecifier[] {
                        user.name.desc(),
                        user.userId.desc()
                };
                break;
            default:
                break;
        }

        return queryFactory.selectFrom(user).where(builder).offset(selectDTO.getSkip()).limit(selectDTO.getTake()).orderBy(sortOrder).fetch();
    }
}
