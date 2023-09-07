package uos.ac.kr.repositories;

import uos.ac.kr.domains.Folder;

import java.util.List;

public interface FolderRepositoryCustom {
    List<Folder> getFoldersFromUserId(int userId);
}
