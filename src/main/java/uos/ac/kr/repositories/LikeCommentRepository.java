package uos.ac.kr.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import uos.ac.kr.domains.LikeCommentId;
import uos.ac.kr.domains.Like_Comment;

public interface LikeCommentRepository extends JpaRepository<Like_Comment, LikeCommentId>, LikeCommentRepositoryCustom{
}
