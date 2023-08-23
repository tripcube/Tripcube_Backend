package uos.ac.kr.controllers;


import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import uos.ac.kr.domains.CustomUserDetails;
import uos.ac.kr.domains.User;
import uos.ac.kr.dtos.GetTodoDTO;
import uos.ac.kr.dtos.GetUserDTO;
import uos.ac.kr.dtos.SelectUserDTO;
import uos.ac.kr.dtos.UpdateUserDTO;
import uos.ac.kr.exceptions.AccessDeniedException;
import uos.ac.kr.exceptions.ResourceNotFoundException;
import uos.ac.kr.mappers.UserMapper;
import uos.ac.kr.repositories.UserRepository;
import uos.ac.kr.responses.BasicResponse;

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
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new Exception("Not found Member with UserId = " + userId));

        GetUserDTO userDTO = UserMapper.INSTANCE.toGetDTO(user);

        BasicResponse<GetUserDTO> response = BasicResponse.<GetUserDTO>builder().code(HttpStatus.CREATED.value()).httpStatus(HttpStatus.CREATED).message("SUCCESS").data(userDTO).build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{userId}/profile")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "프로필 수정", protocols = "http")
    public ResponseEntity<BasicResponse<Null>> updateProfile(@RequestBody HashMap<String, String> map, @PathVariable("userId") int userId) {

        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        int MyuserId = customUserDetails.getUserId();

        if (MyuserId != userId) {
            throw new AccessDeniedException("다른 유저의 정보를 변경할 수 없습니다.");
        }

        User user = userRepo.findById(MyuserId).get();

        Optional<User> userTmp = userRepo.getUserByName(map.get("name"));
        if (!userTmp.isEmpty() && userTmp.get().getName() != user.getName()) {
            throw new AccessDeniedException("이미 존재하는 닉네임입니다.");
        }

        user.setName(map.get("name"));
        user.setOneliner(map.get("oneliner"));

        userRepo.save(user);

        BasicResponse<Null> response = BasicResponse.<Null>builder().code(HttpStatus.CREATED.value()).httpStatus(HttpStatus.CREATED).message("SUCCESS").build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
