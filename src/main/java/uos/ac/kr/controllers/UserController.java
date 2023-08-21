package uos.ac.kr.controllers;


import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uos.ac.kr.domains.User;
import uos.ac.kr.dtos.SelectUserDTO;
import uos.ac.kr.dtos.UpdateUserDTO;
import uos.ac.kr.exceptions.ResourceNotFoundException;
import uos.ac.kr.mappers.UserMapper;
import uos.ac.kr.repositories.UserRepository;
import uos.ac.kr.responses.BasicResponse;

import java.util.List;

@RequiredArgsConstructor
@RestController()
@RequestMapping("/user")
@CrossOrigin(origins = "*")

public class UserController {

    private final UserRepository userRepo;

    @PutMapping("/{num}")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "회원정보", protocols = "http")
    public ResponseEntity<BasicResponse<User>> update(@PathVariable("num") Integer num, @RequestBody UpdateUserDTO userDTO) throws Exception {
        User user = userRepo.findById(num)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Member with MemberNum = " + num));

        UserMapper.INSTANCE.updateFromDto(userDTO, user);

        System.out.println(user.getPassword());

        User newMember = userRepo.save(user);

        BasicResponse<User> response = BasicResponse.<User>builder().code(HttpStatus.OK.value()).httpStatus(HttpStatus.OK).message("SUCCESS").data(newMember).count(1).build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping()
    public List<User> getUsers(@RequestBody SelectUserDTO selectDTO) {
        List<User> users = userRepo.getUsers(selectDTO);

        if (users.isEmpty()) {
            throw new ResourceNotFoundException("Cannot find users.");
        }

        return users;
    }

    @GetMapping("/{num}")
    public User getUser(@PathVariable("num") Integer num) throws Exception {
        User user = userRepo.findById(num)
                .orElseThrow(() -> new Exception("Not found Member with UserId = " + num));

        return user;
    }

}
