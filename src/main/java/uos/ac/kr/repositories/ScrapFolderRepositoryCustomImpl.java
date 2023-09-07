package uos.ac.kr.repositories;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import uos.ac.kr.domains.Scrap_Folder;

import java.util.List;
import java.util.Optional;

import static uos.ac.kr.domains.QScrap_Folder.scrap_Folder;
import static uos.ac.kr.domains.QScrap_Place.scrap_Place;

@RequiredArgsConstructor
public class ScrapFolderRepositoryCustomImpl implements ScrapFolderRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Scrap_Folder> getRecentlyOneFromFolderId(int folderId) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(scrap_Folder.scrapFolderId.folderId.eq(folderId));

        return Optional.ofNullable(queryFactory.selectFrom(scrap_Folder).where(builder).orderBy(scrap_Folder.scrapFolderId.scrapPlaceId.desc()).fetchFirst());
    }

    @Override
    public List<Scrap_Folder> getAllScrapFolderFromFolderId(int folderId) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(scrap_Folder.scrapFolderId.folderId.eq(folderId));

        return queryFactory.selectFrom(scrap_Folder).where(builder).leftJoin(scrap_Folder.scrapPlace, scrap_Place).fetchJoin().fetch();
    }


}
