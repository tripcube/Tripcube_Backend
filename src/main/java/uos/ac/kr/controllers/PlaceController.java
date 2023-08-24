package uos.ac.kr.controllers;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import uos.ac.kr.domains.CustomUserDetails;
import uos.ac.kr.domains.Scrap_Place;
import uos.ac.kr.domains.Todo;
import uos.ac.kr.domains.User;
import uos.ac.kr.dtos.GetLocationPlaceDTO;
import uos.ac.kr.dtos.GetPlaceDTO;
import uos.ac.kr.dtos.GetTodoDTO;
import uos.ac.kr.enums.TodoSortKey;
import uos.ac.kr.exceptions.AccessDeniedException;
import uos.ac.kr.mappers.TodoMapper;
import uos.ac.kr.repositories.PlaceRepository;
import uos.ac.kr.repositories.ScrapPlaceRepository;
import uos.ac.kr.repositories.TodoRepository;
import uos.ac.kr.responses.BasicResponse;
import uos.ac.kr.utils.JsonUtil;

import javax.validation.constraints.Null;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController()
@RequestMapping("/places")
@CrossOrigin(origins = "*")
public class PlaceController {

    private final TodoRepository todoRepo;
    private final ScrapPlaceRepository scrapPlaceRepo;

    @GetMapping("/location")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "좌표 근처 관광지 조회", protocols = "http")
    public ResponseEntity<BasicResponse<GetLocationPlaceDTO>> getLocationPlace(@RequestParam(value = "mapX") double mapX, @RequestParam(value = "mapY") double mapY, @RequestParam("page") int page) {

        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = customUserDetails.getUserId();

        GetLocationPlaceDTO locationPlaceDTO = new GetLocationPlaceDTO(page, new ArrayList<>());
        int totalCount = 0;
        do {
            String XML_STRING = PlaceRepository.getLocationPlace(mapX, mapY, page);
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
                GetPlaceDTO placeDTO = new GetPlaceDTO(item.get("addr1").toString(), item.get("title").toString(), (int)item.get("contentid"), item.get("firstimage").toString(), (double)item.get("mapx"), (double)item.get("mapy"), (int)item.get("contenttypeid"), false);
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
                GetPlaceDTO placeDTO = new GetPlaceDTO(item.get("addr1").toString(), item.get("title").toString(), (int)item.get("contentid"), item.get("firstimage").toString(), (double)item.get("mapx"), (double)item.get("mapy"), (int)item.get("contenttypeid"), false);
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
        Scrap_Place scrapPlace = new Scrap_Place();
        scrapPlace.setUser(user);
        scrapPlace.setPlaceId(placeId);
        scrapPlace.setCreatedAt(new Date());

        scrapPlaceRepo.save(scrapPlace);

        BasicResponse<Null> response = BasicResponse.<Null>builder().code(HttpStatus.CREATED.value()).httpStatus(HttpStatus.CREATED).message("SUCCESS").build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{placeId}/scrap")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "장소 스크랩 하기", protocols = "http")
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
}
