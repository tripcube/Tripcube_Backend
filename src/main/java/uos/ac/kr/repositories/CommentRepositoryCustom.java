package uos.ac.kr.repositories;

import uos.ac.kr.domains.Comment;
import uos.ac.kr.enums.CommentSortKey;

import java.util.List;

public interface CommentRepositoryCustom {
    List<Comment> getComments(int userId, CommentSortKey sortKey, int pages);
}
