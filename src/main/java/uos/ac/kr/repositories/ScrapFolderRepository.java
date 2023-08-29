package uos.ac.kr.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uos.ac.kr.domains.ScrapFolderId;
import uos.ac.kr.domains.Scrap_Folder;

public interface ScrapFolderRepository extends JpaRepository<Scrap_Folder, ScrapFolderId>, ScrapFolderRepositoryCustom{
}
