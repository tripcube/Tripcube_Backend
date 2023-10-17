package uos.ac.kr.controllers;


import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import uos.ac.kr.domains.CustomUserDetails;
import uos.ac.kr.domains.User;
import uos.ac.kr.dtos.*;
import uos.ac.kr.exceptions.AccessDeniedException;
import uos.ac.kr.exceptions.ResourceNotFoundException;
import uos.ac.kr.mappers.UserMapper;
import uos.ac.kr.repositories.UserRepository;
import uos.ac.kr.responses.BasicResponse;

import javax.swing.plaf.PanelUI;
import javax.validation.constraints.Null;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController()
@RequestMapping("/users")
@CrossOrigin(origins = "*")

public class UserController {

    private final UserRepository userRepo;

    @GetMapping("/{userId}")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "마이페이지 유저 프로필 얻기", protocols = "http")
    public ResponseEntity<BasicResponse<GetUserDTO>> getUser(@PathVariable("userId") int userId) throws Exception {

        User user;
        GetUserDTO userDTO;

        if (userId == 0) {
            CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            int MyuserId = customUserDetails.getUserId();
            user = userRepo.findById(MyuserId)
                    .orElseThrow(() -> new Exception("Not found Member with UserId = " + MyuserId));
            userDTO = UserMapper.INSTANCE.toGetDTO(user);

        }
        else {
            user = userRepo.findById(userId)
                    .orElseThrow(() -> new Exception("Not found Member with UserId = " + userId));
            userDTO = UserMapper.INSTANCE.toGetDTO(user);
            userDTO.setLoginId(null);
        }

        BasicResponse<GetUserDTO> response = BasicResponse.<GetUserDTO>builder().code(HttpStatus.CREATED.value()).httpStatus(HttpStatus.CREATED).message("SUCCESS").data(userDTO).build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/profile")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "프로필 수정", protocols = "http")
    public ResponseEntity<BasicResponse<Null>> updateProfile(@RequestBody UpdateUserProfileDTO userProfileDTO) {

        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int MyuserId = customUserDetails.getUserId();

        User user = userRepo.findById(MyuserId).get();

        Optional<User> userTmp = userRepo.getUserByName(userProfileDTO.getName());
        if (!userTmp.isEmpty() && userTmp.get().getName() != user.getName()) {
            throw new AccessDeniedException("이미 존재하는 닉네임입니다.");
        }

        user.setName(userProfileDTO.getName());
        user.setOneliner(userProfileDTO.getOneliner());

        userRepo.save(user);

        BasicResponse<Null> response = BasicResponse.<Null>builder().code(HttpStatus.CREATED.value()).httpStatus(HttpStatus.CREATED).message("SUCCESS").build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/password")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "비밀번호 확인", protocols = "http")
    public ResponseEntity<BasicResponse<Boolean>> checkPassword(@RequestBody GetPasswordDTO passwordDTO) {

        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int MyuserId = customUserDetails.getUserId();

        User user = userRepo.findById(MyuserId).get();

        Boolean value = false;

        if (user.getPassword().equals(passwordDTO.getPassword())) {
            value = true;
        }

        BasicResponse<Boolean> response = BasicResponse.<Boolean>builder().code(HttpStatus.CREATED.value()).httpStatus(HttpStatus.CREATED).message("SUCCESS").data(value).build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/password")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "비밀번호 수정", protocols = "http")
    public ResponseEntity<BasicResponse<Null>> updatePassword(@RequestBody GetPasswordDTO passwordDTO) {

        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int MyuserId = customUserDetails.getUserId();

        User user = userRepo.findById(MyuserId).get();

        if (user.getPassword().equals(passwordDTO.getPassword())) {
            throw new AccessDeniedException("기존의 비밀번호와 동일합니다.");
        }

        user.setPassword(passwordDTO.getPassword());
        userRepo.save(user);

        BasicResponse<Null> response = BasicResponse.<Null>builder().code(HttpStatus.CREATED.value()).httpStatus(HttpStatus.CREATED).message("SUCCESS").build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/profileImage")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "프로필 이미지 수정", protocols = "http")
    public ResponseEntity<BasicResponse<Null>> updateProfileImage(@RequestBody GetUserImageDTO userImageDTO) {

        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int MyuserId = customUserDetails.getUserId();

        User user = userRepo.findById(MyuserId).get();
        user.setProfileImage(userImageDTO.getImageURL());

        userRepo.save(user);

        BasicResponse<Null> response = BasicResponse.<Null>builder().code(HttpStatus.CREATED.value()).httpStatus(HttpStatus.CREATED).message("SUCCESS").build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/backgroundImage")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "배경 이미지 수정", protocols = "http")
    public ResponseEntity<BasicResponse<Null>> updateBackgroundImage(@RequestBody GetUserImageDTO userImageDTO) {

        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int MyuserId = customUserDetails.getUserId();

        User user = userRepo.findById(MyuserId).get();
        user.setBackgroundImage(userImageDTO.getImageURL());

        userRepo.save(user);

        BasicResponse<Null> response = BasicResponse.<Null>builder().code(HttpStatus.CREATED.value()).httpStatus(HttpStatus.CREATED).message("SUCCESS").build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }



    @PostMapping("/fcmToken")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "FCM 토큰 등록", protocols = "http")
    public ResponseEntity<BasicResponse<Null>> insertFCMToken(@RequestBody InsertFCMTokenDTO fcmTokenDTO) {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int MyuserId = customUserDetails.getUserId();

        Optional<User> op = userRepo.findById(MyuserId);
        if (op.isEmpty()) {
            throw new ResourceNotFoundException("유저를 찾을 수 없습니다.");
        }

        User user = op.get();
        user.setFcmToken(fcmTokenDTO.getFcmToken());
        userRepo.save(user);

        BasicResponse<Null> response = BasicResponse.<Null>builder().code(HttpStatus.CREATED.value()).httpStatus(HttpStatus.CREATED).message("SUCCESS").build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
