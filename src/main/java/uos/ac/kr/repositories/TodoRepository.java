package uos.ac.kr.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uos.ac.kr.domains.Todo;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Integer>, TodoRepositoryCustom{
}
