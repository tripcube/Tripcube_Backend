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
import uos.ac.kr.domains.CustomUserDetails;
import uos.ac.kr.domains.Todo;
import uos.ac.kr.domains.User;
import uos.ac.kr.dtos.GetTodoDTO;
import uos.ac.kr.dtos.NewTodoDTO;
import uos.ac.kr.enums.TodoSortKey;
import uos.ac.kr.exceptions.DataFormatException;
import uos.ac.kr.mappers.TodoMapper;
import uos.ac.kr.mappers.UserMapper;
import uos.ac.kr.repositories.PlaceRepository;
import uos.ac.kr.repositories.TodoRepository;
import uos.ac.kr.responses.BasicResponse;

import javax.validation.Valid;
import javax.validation.constraints.Null;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController()
@RequestMapping("/todo")
@CrossOrigin(origins = "*")

public class TodoController {
    private final TodoRepository todoRepo;

    @PostMapping("/new")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "Todo 등록", protocols = "http")
    public ResponseEntity<BasicResponse<Null>> insert(@RequestBody @Valid NewTodoDTO newTodoDTO) {

        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = customUserDetails.getUserId();
        System.out.println(userId);

        Todo newTodo = TodoMapper.INSTANCE.toEntity(newTodoDTO);

        User user = new User();
        user.setUserId(userId);
        newTodo.setCreatedAt(new Date());
        newTodo.setUser(user);
        newTodo.setLikes(0);

        todoRepo.save(newTodo);

        BasicResponse<Null> response = BasicResponse.<Null>builder().code(HttpStatus.CREATED.value()).httpStatus(HttpStatus.CREATED).message("SUCCESS").build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/mypage")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "마이페이지 TODO 조회", protocols = "http")
    public ResponseEntity<BasicResponse<List<GetTodoDTO>>> getTodos(@RequestParam("userId") int userId, @RequestParam("sort") String sortkey, @RequestParam("page") int pages) {

        TodoSortKey todoSortKey = TodoSortKey.valueOf(sortkey);
        List<Todo> todos = todoRepo.getTodos(userId, todoSortKey, pages);
        List<GetTodoDTO> getTodoDTOs = todos.stream().map(TodoMapper.INSTANCE::toDTO).collect(Collectors.toList());

        SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일 등록");
        for(GetTodoDTO dto : getTodoDTOs) {
           dto.setUserId(userId);
           dto.setDate(format.format(dto.getCreatedAt()));
           dto.setPlaceName(PlaceRepository.getPlaceName(dto.getPlaceId()));
        }

        BasicResponse<List<GetTodoDTO>> response = BasicResponse.<List<GetTodoDTO>>builder().code(HttpStatus.CREATED.value()).httpStatus(HttpStatus.CREATED).message("SUCCESS").data(getTodoDTOs).build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
