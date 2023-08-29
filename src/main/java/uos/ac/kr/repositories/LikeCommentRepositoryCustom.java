package uos.ac.kr.repositories;

import uos.ac.kr.domains.Like_Comment;

import java.util.List;

public interface LikeCommentRepositoryCustom {
    public List<Like_Comment> getLikeCommentsFromCommentID(int commentId);
}
