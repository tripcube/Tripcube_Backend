package uos.ac.kr.controllers;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import uos.ac.kr.domains.*;
import uos.ac.kr.dtos.GetCommentDTO;
import uos.ac.kr.dtos.NewCommentDTO;
import uos.ac.kr.enums.CommentSortKey;
import uos.ac.kr.exceptions.AccessDeniedException;
import uos.ac.kr.mappers.CommentMapper;
import uos.ac.kr.repositories.*;
import uos.ac.kr.responses.BasicResponse;

import javax.transaction.Transactional;
import javax.validation.constraints.Null;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController()
@RequestMapping("/comments")
@CrossOrigin(origins = "*")
public class CommentController {

    private final CommentRepository commentRepo;
    private final LikeTodoRepository likeTodoRepo;
    private final LikeCommentRepository likeCommentRepo;
    private final UserRepository userRepo;

    @PostMapping()
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "댓글 등록", protocols = "http")
    public ResponseEntity<BasicResponse<Null>> insert(@RequestBody NewCommentDTO commentDTO) {

        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = customUserDetails.getUserId();

        Comment newComment = new Comment();
        User user = new User();
        user.setUserId(userId);
        Todo todo = new Todo();
        todo.setTodoId(commentDTO.getTodoId());

        newComment.setUser(user);
        newComment.setTodo(todo);
        newComment.setContent(commentDTO.getContent());
        newComment.setCreatedAt(new Date());
        newComment.setUpdatedAt(new Date());
        newComment.setLikes(0);

        commentRepo.save(newComment);

        BasicResponse<Null> response = BasicResponse.<Null>builder().code(HttpStatus.CREATED.value()).httpStatus(HttpStatus.CREATED).message("SUCCESS").build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/mypage")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "마이페이지 댓글 조회", protocols = "http")
    public ResponseEntity<BasicResponse<List<GetCommentDTO>>> getComments(@RequestParam("userId") int userId, @RequestParam("sort") String sortkey, @RequestParam("page") int pages) {

        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int MyuserId = customUserDetails.getUserId();

        // 마이페이지 조회
        if (userId == 0) {
            userId = MyuserId;
        }

        CommentSortKey commentSortKey = CommentSortKey.valueOf(sortkey);
        List<Comment> comments = commentRepo.getCommentsFromUserId(userId, commentSortKey, pages);
        List<GetCommentDTO> getCommentDTOs = comments.stream().map(CommentMapper.INSTANCE::toDTO).collect(Collectors.toList());

        SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일 등록");
        for(int i=0; i<comments.size(); i++) {
            getCommentDTOs.get(i).setTodoId(comments.get(i).getTodo().getTodoId());
            getCommentDTOs.get(i).setTodo_content(comments.get(i).getTodo().getContent());
            getCommentDTOs.get(i).setComment_content(comments.get(i).getContent());
            getCommentDTOs.get(i).setPlaceId(comments.get(i).getTodo().getPlaceId());
            getCommentDTOs.get(i).setPlaceName(comments.get(i).getTodo().getPlaceName());
            getCommentDTOs.get(i).setTag(comments.get(i).getTodo().getTag());
            getCommentDTOs.get(i).setTodo_likes(comments.get(i).getTodo().getLikes());
            getCommentDTOs.get(i).setComment_likes(comments.get(i).getLikes());
            getCommentDTOs.get(i).setDate(format.format(comments.get(i).getCreatedAt()));
            LikeTodoId likeTodoId = new LikeTodoId(MyuserId, comments.get(i).getTodo().getTodoId());
            Optional<Like_Todo> likeTodo = likeTodoRepo.findById(likeTodoId);
            if (!likeTodo.isEmpty()) {
                getCommentDTOs.get(i).setTodo_islike(true);
            }
            LikeCommentId likeCommentId = new LikeCommentId(MyuserId, comments.get(i).getCommentId());
            Optional<Like_Comment> likeComment = likeCommentRepo.findById(likeCommentId);
            if (!likeComment.isEmpty()) {
                getCommentDTOs.get(i).setComment_islike(true);
            }
        }

        BasicResponse<List<GetCommentDTO>> response = BasicResponse.<List<GetCommentDTO>>builder().code(HttpStatus.CREATED.value()).httpStatus(HttpStatus.CREATED).message("SUCCESS").data(getCommentDTOs).build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/{commentId}/like")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "댓글 좋아요 하기", protocols = "http")
    public ResponseEntity<BasicResponse<Null>> like(@PathVariable("commentId") int commentId) {

        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = customUserDetails.getUserId();

        Like_Comment likeComment = new Like_Comment();
        LikeCommentId likeCommentId = new LikeCommentId(userId, commentId);

        Optional<Like_Comment> tmp = likeCommentRepo.findById(likeCommentId);
        if (!tmp.isEmpty()) {
            throw new AccessDeniedException("이미 좋아요를 눌렀습니다.");
        }

        User user = userRepo.findById(userId).get();
        Comment comment = commentRepo.findById(commentId).get();

        if (comment.getUser().getUserId() == userId) {
            throw new AccessDeniedException("내 댓글에 좋아요를 누를 수 없습니다.");
        }

        likeComment.setComment(comment);
        likeComment.setUser(user);
        likeComment.setLikeCommentId(likeCommentId);
        likeCommentRepo.save(likeComment);

        comment.setLikes(comment.getLikes() + 1);
        commentRepo.save(comment);


        BasicResponse<Null> response = BasicResponse.<Null>builder().code(HttpStatus.CREATED.value()).httpStatus(HttpStatus.CREATED).message("SUCCESS").build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }

    @DeleteMapping("/{commentId}/like")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "댓글 좋아요 취소", protocols = "http")
    public ResponseEntity<BasicResponse<Null>> unlike(@PathVariable("commentId") int commentId) {

        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = customUserDetails.getUserId();

        LikeCommentId likeCommentId = new LikeCommentId(userId, commentId);

        Optional<Like_Comment> tmp = likeCommentRepo.findById(likeCommentId);
        if (tmp.isEmpty()) {
            throw new AccessDeniedException("아직 좋아요를 누르지 않았습니다.");
        }

        likeCommentRepo.delete(tmp.get());

        Comment comment = commentRepo.findById(commentId).get();

        comment.setLikes(comment.getLikes() - 1);
        commentRepo.save(comment);

        BasicResponse<Null> response = BasicResponse.<Null>builder().code(HttpStatus.CREATED.value()).httpStatus(HttpStatus.CREATED).message("SUCCESS").build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/todo")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "TODO ID로 댓글 조회", protocols = "http")
    public ResponseEntity<BasicResponse<List<GetCommentDTO>>> getCommentsFromTodoID(@RequestParam("todoId") int todoId, @RequestParam("sort") String sortkey, @RequestParam("page") int pages) {

        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int MyuserId = customUserDetails.getUserId();

        CommentSortKey commentSortKey = CommentSortKey.valueOf(sortkey);
        List<Comment> comments = commentRepo.getCommentsFromTodoID(todoId, commentSortKey, pages);
        List<GetCommentDTO> getCommentDTOs = comments.stream().map(CommentMapper.INSTANCE::toDTO).collect(Collectors.toList());

        SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일 등록");
        for(int i=0; i<comments.size(); i++) {
            User user = comments.get(i).getUser();
            getCommentDTOs.get(i).setUserId(user.getUserId());
            getCommentDTOs.get(i).setUserName(user.getName());
            getCommentDTOs.get(i).setComment_content(comments.get(i).getContent());
            getCommentDTOs.get(i).setComment_likes(comments.get(i).getLikes());
            getCommentDTOs.get(i).setDate(format.format(comments.get(i).getCreatedAt()));
            LikeCommentId likeCommentId = new LikeCommentId(MyuserId, comments.get(i).getCommentId());
            Optional<Like_Comment> likeComment = likeCommentRepo.findById(likeCommentId);
            if (!likeComment.isEmpty()) {
                getCommentDTOs.get(i).setComment_islike(true);
            }
        }

        BasicResponse<List<GetCommentDTO>> response = BasicResponse.<List<GetCommentDTO>>builder().code(HttpStatus.CREATED.value()).httpStatus(HttpStatus.CREATED).message("SUCCESS").data(getCommentDTOs).build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "댓글 삭제", protocols = "http")
    @Transactional
    public ResponseEntity<BasicResponse<Null>> delete(@PathVariable("commentId") int commentId) {

        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int MyuserId = customUserDetails.getUserId();

        Comment comment = commentRepo.findById(commentId).get();

        if (comment.getUser().getUserId() != MyuserId) {
            throw new AccessDeniedException("다른 사람의 댓글을 지울 수 없습니다.");
        }

        commentRepo.delete(comment);

        BasicResponse<Null> response = BasicResponse.<Null>builder().code(HttpStatus.CREATED.value()).httpStatus(HttpStatus.CREATED).message("SUCCESS").build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{commentId}")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "댓글 수정", protocols = "http")
    public ResponseEntity<BasicResponse<Null>> update(@PathVariable("commentId") int commentId, @RequestBody NewCommentDTO newCommentDTO) {

        Comment comment = commentRepo.findById(commentId).get();

        comment.setContent(newCommentDTO.getContent());
        comment.setUpdatedAt(new Date());

        commentRepo.save(comment);

        BasicResponse<Null> response = BasicResponse.<Null>builder().code(HttpStatus.CREATED.value()).httpStatus(HttpStatus.CREATED).message("SUCCESS").build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


}
