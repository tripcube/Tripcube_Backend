package uos.ac.kr.controllers;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import uos.ac.kr.domains.*;
import uos.ac.kr.dtos.GetTagDTO;
import uos.ac.kr.dtos.GetTodoDTO;
import uos.ac.kr.dtos.NewTodoDTO;
import uos.ac.kr.enums.TodoSortKey;
import uos.ac.kr.exceptions.AccessDeniedException;
import uos.ac.kr.exceptions.ResourceNotFoundException;
import uos.ac.kr.key.ChatGPT;
import uos.ac.kr.mappers.TodoMapper;
import uos.ac.kr.repositories.*;
import uos.ac.kr.responses.BasicResponse;
import uos.ac.kr.utils.JsonUtil;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Null;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController()
@RequestMapping(value = "/todos")
@CrossOrigin(origins = "*")

public class TodoController {
    private final TodoRepository todoRepo;
    private final LikeTodoRepository likeTodoRepo;
    private final UserRepository userRepo;
    private final ActivityRepository activityRepo;

    @PostMapping()
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "Todo 등록", protocols = "http")
    @Transactional
    public ResponseEntity<BasicResponse<Null>> insert(@RequestBody @Valid NewTodoDTO newTodoDTO) {

        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = customUserDetails.getUserId();

        Todo newTodo = TodoMapper.INSTANCE.toEntity(newTodoDTO);

        User user = new User();
        user.setUserId(userId);
        newTodo.setCreatedAt(new Date());
        newTodo.setUser(user);
        newTodo.setLikes(0);

        // 관광지 이름, 지역 코드 Todo에 저장
        String XML_STRING = PlaceRepository.getPlaceDetail(newTodo.getPlaceId());
        System.out.println(XML_STRING);
        JSONObject jsonObject = XML.toJSONObject(XML_STRING);
        JSONObject responseJson = (JSONObject) jsonObject.get("response");
        JSONObject body = (JSONObject) responseJson.get("body");
        JSONObject items = (JSONObject) body.get("items");
        JSONObject item = (JSONObject) items.get("item");

        newTodo.setPlaceName(item.get("title").toString());
        newTodo.setAreaCode1((int) item.get("areacode"));
        newTodo.setAreaCode2((int) item.get("sigungucode"));
        System.out.println(newTodo.getPlaceName());
        System.out.println(newTodoDTO.getContent());
        todoRepo.save(newTodo);

        // 활동기록 등록
        Activity activity = Activity.builder()
                .placeId((int) item.get("contentid"))
                .placeName(item.get("title").toString())
                .placeImage(item.get("firstimage").toString())
                .areaCode1((int) item.get("areacode"))
                .areaCode2((int) item.get("sigungucode"))
                .type("TODO")
                .user(user)
                .createdAt(new Date())
                .build();
        activityRepo.save(activity);

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
        List<Todo> todos = todoRepo.getTodosForPlaceId(placeId, null, todoSortKey, pages, limit);
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

    @DeleteMapping ("/{todoId}/like")
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

    @GetMapping ("/{todoId}")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "TODO 상세 정보 조회", protocols = "http")
    public ResponseEntity<BasicResponse<GetTodoDTO>> getTodo(@PathVariable("todoId") int todoId) {

        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int MyUserId = customUserDetails.getUserId();

        Todo todo = todoRepo.findById(todoId).get();
        GetTodoDTO todoDTO = TodoMapper.INSTANCE.toDTO(todo);

        LikeTodoId likeTodoId = new LikeTodoId(MyUserId, todoId);
        Optional<Like_Todo> likeTodo = likeTodoRepo.findById(likeTodoId);
        if (!likeTodo.isEmpty()) {
            todoDTO.setLike(true);
        }

        User user = todo.getUser();
        todoDTO.setUserId(user.getUserId());
        todoDTO.setUserName(user.getName());

        BasicResponse<GetTodoDTO> response = BasicResponse.<GetTodoDTO>builder().code(HttpStatus.CREATED.value()).httpStatus(HttpStatus.CREATED).message("SUCCESS").data(todoDTO).build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/tag")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "태그 얻기", protocols = "http")
    public ResponseEntity<BasicResponse<String>> getTag(@RequestBody @Valid GetTagDTO tagDTO) {

        //분류기준
        String[] category = {"사진", "체험", "산책", "운동", "관람", "음식", "힐링", "지식", "쇼핑"};

        //HTTP Header
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Authorization", "Bearer " + ChatGPT.API_KEY);

        //HTTP Body
        JSONObject params = new JSONObject();
        params.put("model", ChatGPT.MODEL);
        List<JSONObject> msg = new LinkedList<>();
        JSONObject msg1 = new JSONObject();
        JSONObject msg2 = new JSONObject();
        msg1.put("role", "system");
        msg1.put("content", ChatGPT.System_msg);
        msg2.put("role", "user");
        msg2.put("content", "todo: " + tagDTO.getTodo() + " \n ->");
        msg.add(msg1);
        msg.add(msg2);
        params.put("messages", msg);

        // 헤더와 바디 합치기
        HttpEntity<String> entity = new HttpEntity<>(params.toString(), headers);

        // POST 요청보내기
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response2 = rt.exchange(
            "https://api.openai.com/v1/chat/completions",
                HttpMethod.POST,
                entity,
                String.class
        );

        // ChatGPT API Response에서 답변 추출
        String value;
        try {
            JSONParser parser = new JSONParser();
            Object o = parser.parse(response2.getBody());
            org.json.simple.JSONObject object = (org.json.simple.JSONObject) o;
            org.json.simple.JSONArray data =  (org.json.simple.JSONArray) object.get("choices");
            org.json.simple.JSONObject message = (org.json.simple.JSONObject) ((org.json.simple.JSONObject) data.get(0)).get("message");

            String[] strs;
            strs = ((String) message.get("content")).split(" ");
            value = category[Integer.parseInt(strs[1]) - 1];
        }
        catch (Exception e) {
            throw new ResourceNotFoundException("ChatGPT 답변을 불러오는데 실패했습니다.");
        }

        BasicResponse<String> response = BasicResponse.<String>builder().code(HttpStatus.CREATED.value()).httpStatus(HttpStatus.CREATED).message("SUCCESS").data(value).build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


}
