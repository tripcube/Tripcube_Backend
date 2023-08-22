package uos.ac.kr.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uos.ac.kr.domains.Comment;

public interface CommentRepository extends JpaRepository<Comment, Integer>, CommentRepositoryCustom{
}
