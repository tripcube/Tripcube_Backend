package uos.ac.kr.repositories;

import uos.ac.kr.domains.Scrap_Folder;

import java.util.Optional;

public interface ScrapFolderRepositoryCustom {
    Optional<Scrap_Folder> getRecentlyOneFromFolderId(int folderId);
}
