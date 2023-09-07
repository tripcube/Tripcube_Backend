package uos.ac.kr.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uos.ac.kr.domains.Folder;

public interface FolderRepository extends JpaRepository<Folder, Integer>, FolderRepositoryCustom{
}
