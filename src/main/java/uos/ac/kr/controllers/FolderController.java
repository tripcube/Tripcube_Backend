package uos.ac.kr.controllers;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import uos.ac.kr.domains.*;
import uos.ac.kr.dtos.GetFolderDTO;
import uos.ac.kr.dtos.GetScrapPlaceDTO;
import uos.ac.kr.dtos.GetScrapPlaceIdDTO;
import uos.ac.kr.dtos.NewFolderDTO;
import uos.ac.kr.enums.TodoSortKey;
import uos.ac.kr.exceptions.AccessDeniedException;
import uos.ac.kr.exceptions.ResourceNotFoundException;
import uos.ac.kr.mappers.FolderMapper;
import uos.ac.kr.mappers.ScrapPlaceMapper;
import uos.ac.kr.repositories.FolderRepository;
import uos.ac.kr.repositories.ScrapFolderRepository;
import uos.ac.kr.repositories.ScrapPlaceRepository;
import uos.ac.kr.repositories.TodoRepository;
import uos.ac.kr.responses.BasicResponse;

import javax.transaction.Transactional;
import javax.validation.constraints.Null;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController()
@RequestMapping("/folders")
@CrossOrigin(origins = "*")
public class FolderController {

    private final FolderRepository folderRepo;
    private final ScrapFolderRepository scrapFolderRepo;
    private final ScrapPlaceRepository scrapPlaceRepo;
    private final TodoRepository todoRepo;

    @PostMapping()
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "폴더 등록", protocols = "http")
    public ResponseEntity<BasicResponse<Null>> insert(@RequestBody NewFolderDTO folderDTO) {

        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = customUserDetails.getUserId();

        User user = new User();
        user.setUserId(userId);
        Folder folder = Folder.builder()
                .user(user)
                .name(folderDTO.getName())
                .coverImage("")
                .placeCount(0)
                .createdAt(new Date())
                .build();

        folderRepo.save(folder);

        BasicResponse<Null> response = BasicResponse.<Null>builder().code(HttpStatus.CREATED.value()).httpStatus(HttpStatus.CREATED).message("SUCCESS").build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/{folderId}/places")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "폴더에 장소 추가", protocols = "http")
    @Transactional
    public ResponseEntity<BasicResponse<Null>> updatePlace(@PathVariable("folderId") int folderId, @RequestBody GetScrapPlaceIdDTO getScrapPlaceIdDTO) {

        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = customUserDetails.getUserId();

        Folder folder = folderRepo.findById(folderId).get();
        if (folder.getUser().getUserId() != userId) {
            throw new AccessDeniedException("다른 유저의 폴더를 수정할 수 없습니다.");
        }

        // 폴더 배경 사진 고르기
        Collections.sort(getScrapPlaceIdDTO.getScrapPlaceIds());

        for(int i = 0; i< getScrapPlaceIdDTO.getScrapPlaceIds().size(); i++) {
            ScrapFolderId scrapFolderId = new ScrapFolderId(getScrapPlaceIdDTO.getScrapPlaceIds().get(i), folderId);
            Scrap_Folder scrapFolder = new Scrap_Folder();

            Scrap_Place scrapPlace = scrapPlaceRepo.findById(getScrapPlaceIdDTO.getScrapPlaceIds().get(i)).get();
            if (scrapPlace.getUser().getUserId() != userId) {
                throw new AccessDeniedException("다른 유저의 스크랩을 폴더에 추가할 수 없습니다.");
            }

            scrapFolder.setScrapFolderId(scrapFolderId);
            scrapFolder.setFolder(folder);
            scrapFolder.setScrapPlace(scrapPlace);

            scrapFolderRepo.save(scrapFolder);

            // 폴더 배경 사진 고르기
            if (i == getScrapPlaceIdDTO.getScrapPlaceIds().size() - 1) {
                folder.setCoverImage(scrapPlace.getPlaceImage());
            }
        }

        // 폴더 장수 개수 추가
        folder.setPlaceCount(folder.getPlaceCount() + getScrapPlaceIdDTO.getScrapPlaceIds().size());
        folderRepo.save(folder);

        BasicResponse<Null> response = BasicResponse.<Null>builder().code(HttpStatus.CREATED.value()).httpStatus(HttpStatus.CREATED).message("SUCCESS").build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @DeleteMapping("/{folderId}/places")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "폴더에 장소 제거", protocols = "http")
    @Transactional
    public ResponseEntity<BasicResponse<Null>> deletePlace(@PathVariable("folderId") int folderId, @RequestBody GetScrapPlaceIdDTO getScrapPlaceIdDTO) {

        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = customUserDetails.getUserId();

        Folder folder = folderRepo.findById(folderId).get();
        if (folder.getUser().getUserId() != userId) {
            throw new AccessDeniedException("다른 유저의 폴더를 수정할 수 없습니다.");
        }

        for(int i = 0; i< getScrapPlaceIdDTO.getScrapPlaceIds().size(); i++) {
            ScrapFolderId scrapFolderId = new ScrapFolderId(getScrapPlaceIdDTO.getScrapPlaceIds().get(i), folderId);
            Optional<Scrap_Folder> scrapFolder = scrapFolderRepo.findById(scrapFolderId);
            if (scrapFolder.isEmpty()) {
                throw new ResourceNotFoundException("폴더에 없는 장소를 삭제할 수 없습니다.");
            }
            scrapFolderRepo.delete(scrapFolder.get());
        }

        // 폴더 커버 이미지 새로 지정
        Optional<Scrap_Folder> scrapFolder = scrapFolderRepo.getRecentlyOneFromFolderId(folderId);
        if (scrapFolder.isEmpty()) {
            folder.setCoverImage("");
        }
        else {
            Scrap_Place scrapPlace = scrapPlaceRepo.findById(scrapFolder.get().getScrapFolderId().getScrapPlaceId()).get();
            folder.setCoverImage(scrapPlace.getPlaceImage());
        }

        // 폴더 장소 개수 제거
        folder.setPlaceCount(folder.getPlaceCount() - getScrapPlaceIdDTO.getScrapPlaceIds().size());
        folderRepo.save(folder);

        BasicResponse<Null> response = BasicResponse.<Null>builder().code(HttpStatus.CREATED.value()).httpStatus(HttpStatus.CREATED).message("SUCCESS").build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{folderId}/name")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "폴더 이름 변경", protocols = "http")
    public ResponseEntity<BasicResponse<Null>> updateName(@PathVariable("folderId") int folderId, @RequestBody NewFolderDTO newFolderDTO) {

        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = customUserDetails.getUserId();

        Folder folder = folderRepo.findById(folderId).get();
        if (folder.getUser().getUserId() != userId) {
            throw new AccessDeniedException("다른 유저의 폴더에 접근할 수 없습니다.");
        }

        folder.setName(newFolderDTO.getName());
        folderRepo.save(folder);

        BasicResponse<Null> response = BasicResponse.<Null>builder().code(HttpStatus.CREATED.value()).httpStatus(HttpStatus.CREATED).message("SUCCESS").build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{folderId}")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "폴더 삭제", protocols = "http")
    @Transactional
    public ResponseEntity<BasicResponse<Null>> delete(@PathVariable("folderId") int folderId) {

        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = customUserDetails.getUserId();

        Folder folder = folderRepo.findById(folderId).get();
        if (folder.getUser().getUserId() != userId) {
            throw new AccessDeniedException("다른 유저의 폴더에 접근할 수 없습니다.");
        }

        folderRepo.delete(folder);

        BasicResponse<Null> response = BasicResponse.<Null>builder().code(HttpStatus.CREATED.value()).httpStatus(HttpStatus.CREATED).message("SUCCESS").build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "폴더 조회", protocols = "http")
    public ResponseEntity<BasicResponse<List<GetFolderDTO>>> getFolders() {

        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = customUserDetails.getUserId();

        List<Folder> folders = folderRepo.getFoldersFromUserId(userId);

        List<GetFolderDTO> folderDTOS = folders.stream().map(m -> FolderMapper.INSTANCE.toDTO(m)).collect(Collectors.toList());

        BasicResponse<List<GetFolderDTO>> response = BasicResponse.<List<GetFolderDTO>>builder().code(HttpStatus.CREATED.value()).httpStatus(HttpStatus.CREATED).message("SUCCESS").data(folderDTOS).build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{folderId}")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "폴더 상세 조회", protocols = "http")
    public ResponseEntity<BasicResponse<List<GetScrapPlaceDTO>>> getFolderDetail(@PathVariable("folderId") int folderId) {

        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = customUserDetails.getUserId();

        Folder folder = folderRepo.findById(folderId).get();
        if (folder.getUser().getUserId() != userId) {
            throw new AccessDeniedException("다른 유저의 폴더에 접근할 수 없습니다.");
        }

        List<Scrap_Folder> scrapFolders = scrapFolderRepo.getAllScrapFolderFromFolderId(folderId);
        List<GetScrapPlaceDTO> scrapPlaces = scrapFolders.stream().map(m -> ScrapPlaceMapper.INSTANCE.toDTO(m.getScrapPlace())).collect(Collectors.toList());

        for(int i=0; i<scrapPlaces.size(); i++) {
            //태그 불러오기
            ArrayList<String> tags = new ArrayList<>();
            List<Todo> todos = todoRepo.getTodosForPlaceId(scrapPlaces.get(i).getPlaceId(), null, TodoSortKey.LIKE_DESC, 0, 2);
            String firstTag = "";

            for (int j=0; j<todos.size(); j++) {
                if (j == 0) {
                    firstTag = todos.get(j).getTag();
                    tags.add(firstTag);
                }
                else if (!todos.get(j).getTag().equals(firstTag)) {
                    tags.add(todos.get(j).getTag());
                }
            }
            scrapPlaces.get(i).setTags(tags);
        }


        BasicResponse<List<GetScrapPlaceDTO>> response = BasicResponse.<List<GetScrapPlaceDTO>>builder().code(HttpStatus.CREATED.value()).httpStatus(HttpStatus.CREATED).message("SUCCESS").data(scrapPlaces).build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
