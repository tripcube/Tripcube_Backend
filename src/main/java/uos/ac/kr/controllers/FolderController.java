package uos.ac.kr.controllers;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import uos.ac.kr.domains.*;
import uos.ac.kr.dtos.GetScrapPlaceIdDTO;
import uos.ac.kr.dtos.NewFolderDTO;
import uos.ac.kr.exceptions.AccessDeniedException;
import uos.ac.kr.exceptions.ResourceNotFoundException;
import uos.ac.kr.repositories.FolderRepository;
import uos.ac.kr.repositories.ScrapFolderRepository;
import uos.ac.kr.repositories.ScrapPlaceRepository;
import uos.ac.kr.responses.BasicResponse;

import javax.swing.plaf.PanelUI;
import javax.transaction.Transactional;
import javax.validation.constraints.Null;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

@RequiredArgsConstructor
@RestController()
@RequestMapping("/folders")
@CrossOrigin(origins = "*")
public class FolderController {

    private final FolderRepository folderRepo;
    private final ScrapFolderRepository scrapFolderRepo;
    private final ScrapPlaceRepository scrapPlaceRepo;

    @PostMapping()
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "폴더 등록", protocols = "http")
    public ResponseEntity<BasicResponse<Null>> insert(@RequestBody NewFolderDTO folderDTO) {

        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int userId = customUserDetails.getUserId();

        Folder folder = new Folder();
        User user = new User();
        user.setUserId(userId);
        folder.setUser(user);
        folder.setName(folderDTO.getName());
        folder.setCoverImage("");
        folder.setCreatedAt(new Date());

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
                folderRepo.save(folder);
            }
        }

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

        Optional<Scrap_Folder> scrapFolder = scrapFolderRepo.getRecentlyOneFromFolderId(folderId);
        if (scrapFolder.isEmpty()) {
            folder.setCoverImage("");
        }
        else {
            Scrap_Place scrapPlace = scrapPlaceRepo.findById(scrapFolder.get().getScrapFolderId().getScrapPlaceId()).get();
            folder.setCoverImage(scrapPlace.getPlaceImage());
            folderRepo.save(folder);
        }

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

}
