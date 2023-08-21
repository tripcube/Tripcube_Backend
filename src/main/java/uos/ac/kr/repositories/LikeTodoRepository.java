package uos.ac.kr.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uos.ac.kr.domains.LikeTodoId;
import uos.ac.kr.domains.Like_Todo;

public interface LikeTodoRepository extends JpaRepository<Like_Todo, LikeTodoId>, LikeTodoRepositoryCustom{
}
