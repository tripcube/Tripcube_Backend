package uos.ac.kr.repositories;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import uos.ac.kr.domains.Folder;

import java.util.List;

import static uos.ac.kr.domains.QFolder.folder;

@RequiredArgsConstructor
public class FolderRepositoryCustomImpl implements FolderRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Folder> getFoldersFromUserId(int userId) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(folder.user.userId.eq(userId));

        return queryFactory.selectFrom(folder).where(builder).orderBy(folder.createdAt.desc()).fetch();
    }

}
