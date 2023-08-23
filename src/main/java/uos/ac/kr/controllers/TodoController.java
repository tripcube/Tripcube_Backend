package uos.ac.kr.controllers;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import uos.ac.kr.domains.*;
import uos.ac.kr.dtos.GetTodoDTO;
import uos.ac.kr.dtos.NewTodoDTO;
import uos.ac.kr.enums.TodoSortKey;
import uos.ac.kr.exceptions.AccessDeniedException;
import uos.ac.kr.exceptions.DataFormatException;
import uos.ac.kr.exceptions.ResourceNotFoundException;
import uos.ac.kr.mappers.TodoMapper;
import uos.ac.kr.mappers.UserMapper;
import uos.ac.kr.repositories.*;
import uos.ac.kr.responses.BasicResponse;

import javax.validation.Valid;
import javax.validation.constraints.Null;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController()
@RequestMapping("/todos")
@CrossOrigin(origins = "*")

public class TodoController {
    private final TodoRepository todoRepo;
    private final LikeTodoRepository likeTodoRepo;
    private final UserRepository userRepo;

    @PostMapping()
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "Todo 등록", protocols = "http")
    public ResponseEntity<BasicResponse<Null>> insert(@RequestBody @Valid NewTodoDTO newTodoDTO) {

        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = customUserDetails.getUserId();

        Todo newTodo = TodoMapper.INSTANCE.toEntity(newTodoDTO);

        User user = new User();
        user.setUserId(userId);
        newTodo.setCreatedAt(new Date());
        newTodo.setUser(user);
        newTodo.setLikes(0);
        newTodo.setPlaceName(PlaceRepository.getPlaceName(newTodo.getPlaceId()));

        todoRepo.save(newTodo);

        BasicResponse<Null> response = BasicResponse.<Null>builder().code(HttpStatus.CREATED.value()).httpStatus(HttpStatus.CREATED).message("SUCCESS").build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/mypage")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "마이페이지 TODO 조회", protocols = "http")
    public ResponseEntity<BasicResponse<List<GetTodoDTO>>> getTodos(@RequestParam("userId") int userId, @RequestParam("sort") String sortkey, @RequestParam("page") int pages) {

        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int MyuserId = customUserDetails.getUserId();

        TodoSortKey todoSortKey = TodoSortKey.valueOf(sortkey);
        List<Todo> todos = todoRepo.getTodosForUserId(userId, todoSortKey, pages, 10);
        List<GetTodoDTO> getTodoDTOs = todos.stream().map(TodoMapper.INSTANCE::toDTO).collect(Collectors.toList());

        SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일 등록");
        for(GetTodoDTO dto : getTodoDTOs) {
            LikeTodoId likeTodoId = new LikeTodoId(MyuserId, dto.getTodoId());
           Optional<Like_Todo> likeTodo= likeTodoRepo.findById(likeTodoId);
           if (!likeTodo.isEmpty()) {
               dto.setLike(true);
           }
           dto.setUserId(userId);
           dto.setDate(format.format(dto.getCreatedAt()));
        }

        BasicResponse<List<GetTodoDTO>> response = BasicResponse.<List<GetTodoDTO>>builder().code(HttpStatus.CREATED.value()).httpStatus(HttpStatus.CREATED).message("SUCCESS").data(getTodoDTOs).build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/place")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "장소 ID로 TODO 조회", protocols = "http")
    public ResponseEntity<BasicResponse<List<GetTodoDTO>>> getTodosForPlaceId(@RequestParam("placeId") int placeId, @RequestParam("sort") String sortkey, @RequestParam("page") int pages, @RequestParam("limit") int limit) {

        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int MyuserId = customUserDetails.getUserId();

        TodoSortKey todoSortKey = TodoSortKey.valueOf(sortkey);
        List<Todo> todos = todoRepo.getTodosForPlaceId(placeId, todoSortKey, pages, limit);
        List<GetTodoDTO> getTodoDTOs = todos.stream().map(TodoMapper.INSTANCE::toDTO).collect(Collectors.toList());

        for(GetTodoDTO dto : getTodoDTOs) {
            LikeTodoId likeTodoId = new LikeTodoId(MyuserId, dto.getTodoId());
            Optional<Like_Todo> likeTodo= likeTodoRepo.findById(likeTodoId);
            if (!likeTodo.isEmpty()) {
                dto.setLike(true);
            }
        }

        BasicResponse<List<GetTodoDTO>> response = BasicResponse.<List<GetTodoDTO>>builder().code(HttpStatus.CREATED.value()).httpStatus(HttpStatus.CREATED).message("SUCCESS").data(getTodoDTOs).build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/{todoId}/like")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "TODO 좋아요 하기", protocols = "http")
    public ResponseEntity<BasicResponse<Null>> likeTodo(@PathVariable("todoId") int todoId) {

        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = customUserDetails.getUserId();

        Like_Todo likeTodo = new Like_Todo();
        LikeTodoId likeTodoId = new LikeTodoId();
        likeTodoId.setTodoId(todoId);
        likeTodoId.setUserId(userId);

        Optional<Like_Todo> tmp = likeTodoRepo.findById(likeTodoId);
        if (!tmp.isEmpty()) {
            throw new AccessDeniedException("이미 좋아요를 눌렀습니다.");
        }

        User user = userRepo.findById(userId).get();
        Todo todo = todoRepo.findById(todoId).get();

        if (todo.getUser().getUserId() == userId) {
            throw new AccessDeniedException("내 TODO에 좋아요를 누를 수 없습니다.");
        }

        likeTodo.setTodo(todo);
        likeTodo.setUser(user);
        likeTodo.setLikeTodoId(likeTodoId);
        likeTodoRepo.save(likeTodo);

        todo.setLikes(todo.getLikes() + 1);
        todoRepo.save(todo);

        BasicResponse<Null> response = BasicResponse.<Null>builder().code(HttpStatus.CREATED.value()).httpStatus(HttpStatus.CREATED).message("SUCCESS").build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/{todoId}/unlike")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "TODO 좋아요 취소", protocols = "http")
    public ResponseEntity<BasicResponse<Null>> unlikeTodo(@PathVariable("todoId") int todoId) {

        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = customUserDetails.getUserId();

        LikeTodoId likeTodoId = new LikeTodoId();
        likeTodoId.setTodoId(todoId);
        likeTodoId.setUserId(userId);

        Optional<Like_Todo> tmp = likeTodoRepo.findById(likeTodoId);
        if (tmp.isEmpty()) {
            throw new AccessDeniedException("아직 좋아요를 누르지 않았습니다.");
        }

        likeTodoRepo.delete(tmp.get());

        Todo todo = todoRepo.findById(todoId).get();

        todo.setLikes(todo.getLikes() - 1);
        todoRepo.save(todo);

        BasicResponse<Null> response = BasicResponse.<Null>builder().code(HttpStatus.CREATED.value()).httpStatus(HttpStatus.CREATED).message("SUCCESS").build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


}
