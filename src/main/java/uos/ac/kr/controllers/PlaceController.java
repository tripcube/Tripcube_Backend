package uos.ac.kr.controllers;

import com.querydsl.core.Tuple;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.json.simple.parser.JSONParser;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import uos.ac.kr.domains.*;
import uos.ac.kr.dtos.GetLocationPlaceDTO;
import uos.ac.kr.dtos.GetMainPlaceDTO;
import uos.ac.kr.dtos.GetPlaceDTO;
import uos.ac.kr.dtos.GetScrapPlaceDTO;
import uos.ac.kr.enums.TodoSortKey;
import uos.ac.kr.exceptions.AccessDeniedException;
import uos.ac.kr.exceptions.ResourceNotFoundException;
import uos.ac.kr.key.ChatGPT;
import uos.ac.kr.mappers.ScrapPlaceMapper;
import uos.ac.kr.repositories.ActivityRepository;
import uos.ac.kr.repositories.PlaceRepository;
import uos.ac.kr.repositories.ScrapPlaceRepository;
import uos.ac.kr.repositories.TodoRepository;
import uos.ac.kr.responses.BasicResponse;
import uos.ac.kr.utils.JsonUtil;

import javax.validation.constraints.Null;
import java.util.*;
import java.util.stream.Collectors;
import reactor.core.scheduler.Schedulers;

@RequiredArgsConstructor
@RestController()
@RequestMapping("/places")
@CrossOrigin(origins = "*")
public class PlaceController {

    private final TodoRepository todoRepo;
    private final ScrapPlaceRepository scrapPlaceRepo;
    private final ActivityRepository activityRepo;

    @GetMapping("/location")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "좌표 근처 관광지 조회", protocols = "http")
    public ResponseEntity<BasicResponse<GetLocationPlaceDTO>> getLocationPlace(@RequestParam(value = "mapX") double mapX, @RequestParam(value = "mapY") double mapY, @RequestParam("page") int page) {
        // JWT 토큰 인증정보로부터 userId GET
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = customUserDetails.getUserId();

        // 총 장소의 개수가 10개가 될 때까지 Tour API 호출 (숙박, 식당, 카페, 쇼핑은 제외)
        GetLocationPlaceDTO locationPlaceDTO = new GetLocationPlaceDTO(page, new ArrayList<>());
        int totalCount = 0;
        do {
            // Tour API 호출 후 XML 데이터 JOSN으로 변환
            String XML_STRING = PlaceRepository.getLocationPlace(mapX, mapY, page);
            JSONObject jsonObject = XML.toJSONObject(XML_STRING);
            JSONObject responseJson = (JSONObject) jsonObject.get("response");
            JSONObject body = (JSONObject) responseJson.get("body");
            totalCount += (int) body.get("numOfRows");
            // 더 이상 불러올 데이터가 없다면 break
            if (((int) body.get("numOfRows")) == 0) {
                break;
            }

            JSONObject items = (JSONObject) body.get("items");
            JSONArray itemArray = JsonUtil.covertJsonObjectToJsonArray(items.get("item"));

            // 장소 하나하나 DTO로 변환
            for(int i=0; i<itemArray.length(); i++) {
                JSONObject item = (JSONObject) itemArray.get(i);
                if ((int) item.get("contenttypeid") == 38 || (int) item.get("contenttypeid") == 39 || (int) item.get("contenttypeid") == 32) {
                    totalCount -= 1;
                    continue;
                }
                GetPlaceDTO placeDTO = new GetPlaceDTO();
                placeDTO.setAddress(item.get("addr1").toString());
                placeDTO.setPlaceName(item.get("title").toString());
                placeDTO.setPlaceId((int)item.get("contentid"));
                placeDTO.setImage(item.get("firstimage").toString());
                placeDTO.setMapX((double)item.get("mapx"));
                placeDTO.setMapY((double)item.get("mapy"));
                placeDTO.setContenttype((int)item.get("contenttypeid"));
                if (!scrapPlaceRepo.getDuplicateOne(userId, (int)item.get("contentid")).isEmpty()) {
                    placeDTO.setScrap(true);
                }
                ArrayList<GetPlaceDTO> preList = locationPlaceDTO.getPlaces();
                preList.add(placeDTO);
                locationPlaceDTO.setPlaces(preList);
            }
            page += 1;
        } while (totalCount < 10);

        locationPlaceDTO.setPage(page);

        BasicResponse<GetLocationPlaceDTO> response = BasicResponse.<GetLocationPlaceDTO>builder().code(HttpStatus.CREATED.value()).httpStatus(HttpStatus.CREATED).message("SUCCESS").data(locationPlaceDTO).count(totalCount).build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/keyword")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "키워드로 관광지 조회", protocols = "http")
    public ResponseEntity<BasicResponse<GetLocationPlaceDTO>> getKeywordPlace(@RequestParam(value = "keyword") String keyword, @RequestParam("page") int page) {

        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = customUserDetails.getUserId();

        GetLocationPlaceDTO locationPlaceDTO = new GetLocationPlaceDTO(page, new ArrayList<>());
        int totalCount = 0;
        do {
            String XML_STRING = PlaceRepository.getKeywordPlace(keyword, page);
            JSONObject jsonObject = XML.toJSONObject(XML_STRING);
            JSONObject responseJson = (JSONObject) jsonObject.get("response");
            JSONObject body = (JSONObject) responseJson.get("body");
            totalCount += (int) body.get("numOfRows");
            if (((int) body.get("numOfRows")) == 0) {
                break;
            }

            JSONObject items = (JSONObject) body.get("items");
            JSONArray itemArray = JsonUtil.covertJsonObjectToJsonArray(items.get("item"));


            for(int i=0; i<itemArray.length(); i++) {
                JSONObject item = (JSONObject) itemArray.get(i);
                if ((int) item.get("contenttypeid") == 38 || (int) item.get("contenttypeid") == 39 || (int) item.get("contenttypeid") == 32) {
                    totalCount -= 1;
                    continue;
                }
                GetPlaceDTO placeDTO = new GetPlaceDTO();
                placeDTO.setAddress(item.get("addr1").toString());
                placeDTO.setPlaceName(item.get("title").toString());
                placeDTO.setPlaceId((int)item.get("contentid"));
                placeDTO.setImage(item.get("firstimage").toString());
                placeDTO.setMapX((double)item.get("mapx"));
                placeDTO.setMapY((double)item.get("mapy"));
                placeDTO.setContenttype((int)item.get("contenttypeid"));
                if (!scrapPlaceRepo.getDuplicateOne(userId, (int)item.get("contentid")).isEmpty()) {
                    placeDTO.setScrap(true);
                }
                ArrayList<GetPlaceDTO> preList = locationPlaceDTO.getPlaces();
                preList.add(placeDTO);
                locationPlaceDTO.setPlaces(preList);
            }
            page += 1;
        } while (totalCount < 10);

        locationPlaceDTO.setPage(page);

        BasicResponse<GetLocationPlaceDTO> response = BasicResponse.<GetLocationPlaceDTO>builder().code(HttpStatus.CREATED.value()).httpStatus(HttpStatus.CREATED).message("SUCCESS").data(locationPlaceDTO).count(totalCount).build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/{placeId}/scrap")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "장소 스크랩 하기", protocols = "http")
    public ResponseEntity<BasicResponse<Null>> scrap(@PathVariable("placeId") int placeId) {

        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int MyuserId = customUserDetails.getUserId();

        if (!scrapPlaceRepo.getDuplicateOne(MyuserId, placeId).isEmpty()) {
            throw new AccessDeniedException("이미 스크랩을 하였습니다.");
        }

        User user = new User();
        user.setUserId(MyuserId);
        String XML_STRING = PlaceRepository.getPlaceDetail(placeId);
        JSONObject jsonObject = XML.toJSONObject(XML_STRING);
        JSONObject responseJson = (JSONObject) jsonObject.get("response");
        JSONObject body = (JSONObject) responseJson.get("body");
        JSONObject items = (JSONObject) body.get("items");
        JSONObject item = (JSONObject) items.get("item");

        Scrap_Place scrapPlace = new Scrap_Place();
        scrapPlace.setUser(user);
        scrapPlace.setPlaceId(placeId);
        scrapPlace.setCreatedAt(new Date());
        scrapPlace.setPlaceName(item.get("title").toString());
        scrapPlace.setPlaceAddress(item.get("addr1").toString());
        scrapPlace.setPlaceImage(item.get("firstimage").toString());

        scrapPlaceRepo.save(scrapPlace);

        BasicResponse<Null> response = BasicResponse.<Null>builder().code(HttpStatus.CREATED.value()).httpStatus(HttpStatus.CREATED).message("SUCCESS").build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{placeId}/scrap")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "장소 스크랩 삭제", protocols = "http")
    public ResponseEntity<BasicResponse<Null>> unScrap(@PathVariable("placeId") int placeId) {

        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int MyuserId = customUserDetails.getUserId();

        Optional<Scrap_Place> scrapPlace = scrapPlaceRepo.getDuplicateOne(MyuserId, placeId);

        if (scrapPlace.isEmpty()) {
            throw new AccessDeniedException("아직 스크랩을 하지 않았습니다.");
        }

        scrapPlaceRepo.delete(scrapPlace.get());

        BasicResponse<Null> response = BasicResponse.<Null>builder().code(HttpStatus.CREATED.value()).httpStatus(HttpStatus.CREATED).message("SUCCESS").build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{placeId}")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "장소 상세 정보", protocols = "http")
    public  ResponseEntity<BasicResponse<GetPlaceDTO>> getPlace(@PathVariable("placeId") int placeId) {

        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int MyuserId = customUserDetails.getUserId();

        //공통정보
        String XML_STRING = PlaceRepository.getPlaceDetail(placeId);
        JSONObject jsonObject = XML.toJSONObject(XML_STRING);
        JSONObject responseJson = (JSONObject) jsonObject.get("response");
        JSONObject body = (JSONObject) responseJson.get("body");
        JSONObject items = (JSONObject) body.get("items");
        JSONObject item = (JSONObject) items.get("item");

        GetPlaceDTO placeDTO = new GetPlaceDTO();
        placeDTO.setPlaceName(item.get("title").toString());
        placeDTO.setPlaceId((int) item.get("contentid"));
        placeDTO.setImage(item.get("firstimage").toString());
        placeDTO.setAddress(item.get("addr1").toString());
        placeDTO.setMapX((double) item.get("mapx"));
        placeDTO.setMapY((double) item.get("mapy"));
        placeDTO.setContenttype((int) item.get("contenttypeid"));
        placeDTO.setTel(item.get("tel").toString());
        placeDTO.setWebsite(item.get("homepage").toString());

        JSONObject detailItem = item;

        //소개정보
        Mono<String> XML_STRING2 = PlaceRepository.getPlaceDetail2(placeId, placeDTO.getContenttype()).subscribeOn(Schedulers.boundedElastic());
        Mono<String> XML_STRING3 = PlaceRepository.getPlaceImages(placeId).subscribeOn(Schedulers.boundedElastic());
        Tuple2<String, String> XML_DATA = Mono.zip(XML_STRING2, XML_STRING3).block();

        jsonObject = XML.toJSONObject(XML_DATA.getT1());
        responseJson = (JSONObject) jsonObject.get("response");
        body = (JSONObject) responseJson.get("body");
        items = (JSONObject) body.get("items");
        item = (JSONObject) items.get("item");

        placeDTO.setUseTime(item.get("usetime").toString());
        placeDTO.setParking(item.get("parking").toString());

        //사진정보
        jsonObject = XML.toJSONObject(XML_DATA.getT2());
        responseJson = (JSONObject) jsonObject.get("response");
        body = (JSONObject) responseJson.get("body");
        items = (JSONObject) body.get("items");
        JSONArray itemArray = JsonUtil.covertJsonObjectToJsonArray(items.get("item"));

        ArrayList<String> images = new ArrayList<>();
        for(int i=0; i<itemArray.length(); i++) {
            images.add(((JSONObject)itemArray.get(i)).get("originimgurl").toString());
        }
        placeDTO.setMoreImages(images);

        // 스크랩 여부 체크
        Optional<Scrap_Place> scrapPlace = scrapPlaceRepo.getDuplicateOne(MyuserId, placeId);
        if (!scrapPlace.isEmpty()) {
            placeDTO.setScrap(true);
        }

        //태그 불러오기
        ArrayList<Integer> tags = new ArrayList<>();
        List<Todo> todos = todoRepo.getTodosForPlaceId(placeId, 0, TodoSortKey.LIKE_DESC, 1, 2);
        int firstTag = 0;

        for (int i=0; i<todos.size(); i++) {
            if (i == 0) {
                firstTag = todos.get(i).getTag();
                tags.add(firstTag);
            }
            else if (todos.get(i).getTag() != firstTag) {
                tags.add(todos.get(i).getTag());
            }
        }
        placeDTO.setTags(tags);

        // 활동기록 등록
        User user = new User();
        user.setUserId(MyuserId);
        Activity activity = Activity.builder()
                .placeId((int) detailItem.get("contentid"))
                .placeName(detailItem.get("title").toString())
                .placeImage(detailItem.get("firstimage").toString())
                .areaCode1((int) detailItem.get("areacode"))
                .areaCode2((int) detailItem.get("sigungucode"))
                .type("SER")
                .user(user)
                .createdAt(new Date())
                .build();

        activityRepo.save(activity);

        BasicResponse<GetPlaceDTO> response = BasicResponse.<GetPlaceDTO>builder().code(HttpStatus.CREATED.value()).httpStatus(HttpStatus.CREATED).message("SUCCESS").data(placeDTO).build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/scrap")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "스크랩한 장소 보기", protocols = "http")
    public ResponseEntity<BasicResponse<List<GetScrapPlaceDTO>>> getScrapPlace(@RequestParam("page") int pages) {

        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int MyUserId = customUserDetails.getUserId();

        List<Scrap_Place> scrapPlaces = scrapPlaceRepo.getScrapPlace(MyUserId, pages);
        List<GetScrapPlaceDTO> placeDTOS = scrapPlaces.stream().map(ScrapPlaceMapper.INSTANCE::toDTO).collect(Collectors.toList());

        for(int i=0; i<placeDTOS.size(); i++) {
            //태그 불러오기
            ArrayList<Integer> tags = new ArrayList<>();
            List<Todo> todos = todoRepo.getTodosForPlaceId(scrapPlaces.get(i).getPlaceId(), 0, TodoSortKey.LIKE_DESC, 1, 2);
            int firstTag = 0;

            for (int j=0; j<todos.size(); j++) {
                if (j == 0) {
                    firstTag = todos.get(j).getTag();
                    tags.add(firstTag);
                }
                else if (todos.get(j).getTag() != firstTag) {
                    tags.add(todos.get(j).getTag());
                }
            }
            placeDTOS.get(i).setTags(tags);
        }


        BasicResponse<List<GetScrapPlaceDTO>> response = BasicResponse.<List<GetScrapPlaceDTO>>builder().code(HttpStatus.CREATED.value()).httpStatus(HttpStatus.CREATED).message("SUCCESS").data(placeDTOS).build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @GetMapping("/recommend/hot-place")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "24시간 인기 장소", protocols = "http")
    public ResponseEntity<BasicResponse<List<GetMainPlaceDTO>>> getHotPlaces(@RequestParam("areaCode1") int areaCode1, @RequestParam("areaCode2") int areaCode2, @RequestParam("page") int page) {

        List<Integer> placeIds = activityRepo.getHotActivity(areaCode1, areaCode2, page);
        List<GetMainPlaceDTO> placeDTOS = new LinkedList<>();
        for (int i : placeIds) {
            //태그 불러오기
            ArrayList<Integer> tags = new ArrayList<>();
            List<Todo> todos = todoRepo.getTodosForPlaceId(i, 0, TodoSortKey.LIKE_DESC, 1, 2);
            int firstTag = 0;

            for (int j=0; j<todos.size(); j++) {
                if (j == 0) {
                    firstTag = todos.get(j).getTag();
                    tags.add(firstTag);
                }
                else if (todos.get(j).getTag() != firstTag) {
                    tags.add(todos.get(j).getTag());
                }
            }
            Activity activity = activityRepo.getOneActivity(i);
            GetMainPlaceDTO dto = GetMainPlaceDTO.builder()
                    .placeId(i)
                    .placeName(activity.getPlaceName())
                    .placeImage(activity.getPlaceImage())
                    .tags(tags)
                    .build();
            placeDTOS.add(dto);
        }

        BasicResponse<List<GetMainPlaceDTO>> response = BasicResponse.<List<GetMainPlaceDTO>>builder().code(HttpStatus.CREATED.value()).httpStatus(HttpStatus.CREATED).message("SUCCESS").data(placeDTOS).build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/recommend/todo-place")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "OO하기 좋은 장소", protocols = "http")
    public ResponseEntity<BasicResponse<List<GetMainPlaceDTO>>> getTodoPlaces(@RequestParam("areaCode1") int areaCode1, @RequestParam("areaCode2") int areaCode2, @RequestParam("page") int page, @RequestParam("tag") int tag) {

        List<Integer> placeIds = todoRepo.getPlaceIdFromAreaCode(areaCode1, areaCode2, page, tag);
        List<GetMainPlaceDTO> placeDTOS = new LinkedList<>();
        for (int i : placeIds) {
            Activity activity = activityRepo.getOneActivity(i);
            List<Todo> todo = todoRepo.getTodosForPlaceId(i, tag, TodoSortKey.LIKE_DESC, 1, 1);
            GetMainPlaceDTO dto = GetMainPlaceDTO.builder()
                    .placeId(i)
                    .placeName(activity.getPlaceName())
                    .placeImage(activity.getPlaceImage())
                    .content(todo.get(0).getContent())
                    .build();
            placeDTOS.add(dto);
        }

        BasicResponse<List<GetMainPlaceDTO>> response = BasicResponse.<List<GetMainPlaceDTO>>builder().code(HttpStatus.CREATED.value()).httpStatus(HttpStatus.CREATED).message("SUCCESS").data(placeDTOS).build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/recommend/like-place")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "좋아할 장소 추천", protocols = "http")
    public ResponseEntity<BasicResponse<List<GetMainPlaceDTO>>> getLikePlaces(@RequestParam("page") int page) {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int MyUserId = customUserDetails.getUserId();

        //사용자가 작성한 TODO로 Text 작성
        List<Todo> todos = todoRepo.getTodosForUserId(MyUserId, TodoSortKey.LIKE_DESC, 1, 10);
        String text = "";
        for(Todo t : todos) {
            text += t.getContent();
            text += ", ";
        }

        //AI 서버 요청
        //HTTP Header
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        //HTTP Body
        JSONObject params = new JSONObject();
        params.put("text", text);
        params.put("page", page);

        // 헤더와 바디 합치기
        HttpEntity<String> entity = new HttpEntity<>(params.toString(), headers);

        // POST 요청보내기
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response2 = rt.exchange(
                "http://localhost:8000/recommand",
                HttpMethod.POST,
                entity,
                String.class
        );

        // AI API Response에서 답변 추출
        System.out.println("hear!");
        List<Integer> placeIds = new LinkedList<>();
        try {
            JSONParser parser = new JSONParser();
            Object o = parser.parse(response2.getBody());
            org.json.simple.JSONArray array = (org.json.simple.JSONArray) o;
            for(Object object : array) {
                placeIds.add(Integer.parseInt(((org.json.simple.JSONObject) object).get("placeId").toString()));
            }
        }
        catch (Exception e) {
            throw new ResourceNotFoundException(e + "AI 서버에서 답변을 불러오는데 실패했습니다.");
        }

        //PlaceId를 바탕으로 Tour API로 정보 불러오기
        List<GetMainPlaceDTO> placeDTOS = new LinkedList<>();
        for(int i : placeIds) {
            //공통정보
            String XML_STRING = PlaceRepository.getPlaceDetail(i);
            JSONObject jsonObject = XML.toJSONObject(XML_STRING);
            JSONObject responseJson = (JSONObject) jsonObject.get("response");
            JSONObject body = (JSONObject) responseJson.get("body");
            JSONObject items = (JSONObject) body.get("items");
            JSONObject item = (JSONObject) items.get("item");

            //태그 불러오기
            ArrayList<Integer> tags = new ArrayList<>();
            List<Todo> t = todoRepo.getTodosForPlaceId(i, 0, TodoSortKey.LIKE_DESC, 1, 2);
            int firstTag = 0;

            for (int j=0; j<t.size(); j++) {
                if (j == 0) {
                    firstTag = t.get(j).getTag();
                    tags.add(firstTag);
                }
                else if (t.get(j).getTag() != firstTag) {
                    tags.add(t.get(j).getTag());
                }
            }

            GetMainPlaceDTO placeDTO = GetMainPlaceDTO.builder()
                    .placeId(i)
                    .placeName(item.get("title").toString())
                    .placeImage(item.get("firstimage").toString())
                    .tags(tags)
                    .build();
            placeDTOS.add(placeDTO);
        }

        BasicResponse<List<GetMainPlaceDTO>> response = BasicResponse.<List<GetMainPlaceDTO>>builder().code(HttpStatus.CREATED.value()).httpStatus(HttpStatus.CREATED).message("SUCCESS").data(placeDTOS).build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
