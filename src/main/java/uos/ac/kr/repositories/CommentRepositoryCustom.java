package uos.ac.kr.repositories;

import uos.ac.kr.domains.Comment;
import uos.ac.kr.enums.CommentSortKey;

import java.util.List;

public interface CommentRepositoryCustom {
    List<Comment> getCommentsFromUserId(int userId, CommentSortKey sortKey, int pages);

    List<Comment> getCommentsFromTodoID(int todoId, CommentSortKey sortKey, int pages);
}
