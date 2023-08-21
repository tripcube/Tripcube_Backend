package uos.ac.kr.controllers;

import io.jsonwebtoken.Claims;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import uos.ac.kr.domains.RefreshToken;
import uos.ac.kr.domains.User;
import uos.ac.kr.dtos.InsertUserDTO;
import uos.ac.kr.dtos.LoginDTO;
import uos.ac.kr.dtos.LogoutDTO;
import uos.ac.kr.dtos.RenewDTO;
import uos.ac.kr.exceptions.AccessDeniedException;
import uos.ac.kr.exceptions.DataFormatException;
import uos.ac.kr.mappers.UserMapper;
import uos.ac.kr.repositories.RefreshTokenRepository;
import uos.ac.kr.repositories.UserRepository;
import uos.ac.kr.responses.AuthResponse;
import uos.ac.kr.responses.BasicResponse;
import uos.ac.kr.utils.JwtTokenProvider;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController()
@RequestMapping("/auth")
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepo;
    private final RefreshTokenRepository refreshTokenRepository;

    @PostMapping("/new")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "회원가입", protocols = "http")
    public ResponseEntity<BasicResponse<User>> insert(@RequestBody @Valid InsertUserDTO userDTO) {
        String loginId = userDTO.getLoginId(), name = userDTO.getName();

        if (userRepo.getDuplicateOne(loginId, name).isPresent()) {
            throw new DataFormatException("Duplicated id or name");
        }

        User newUser = UserMapper.INSTANCE.toEntity(userDTO);

        newUser.setCreatedAt(new Date());

        newUser = userRepo.save(newUser);

        BasicResponse<User> response = BasicResponse.<User>builder().code(HttpStatus.CREATED.value()).httpStatus(HttpStatus.CREATED).message("SUCCESS").data(newUser).count(1).build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Transactional()
    @PostMapping("/new/check-name")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "회원가입 시 닉네임 중복 체크", protocols = "http")
    public ResponseEntity<BasicResponse<Boolean>> checkName(@RequestBody InsertUserDTO insertUserDTO) {

        if (userRepo.getUserByName(insertUserDTO.getName()).isPresent()) {
            BasicResponse<Boolean> response = BasicResponse.<Boolean>builder().code(HttpStatus.OK.value()).httpStatus(HttpStatus.OK).message("SUCCESS").data(false).build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else {
            BasicResponse<Boolean> response = BasicResponse.<Boolean>builder().code(HttpStatus.OK.value()).httpStatus(HttpStatus.OK).message("SUCCESS").data(true).build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @Transactional()
    @PostMapping("/new/check-id")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "회원가입 시 ID 중복 체크", protocols = "http")
    public ResponseEntity<BasicResponse<Boolean>> checkID(@RequestBody InsertUserDTO insertUserDTO) {

        if (userRepo.getUserById(insertUserDTO.getLoginId()).isPresent()) {
            BasicResponse<Boolean> response = BasicResponse.<Boolean>builder().code(HttpStatus.OK.value()).httpStatus(HttpStatus.OK).message("SUCCESS").data(false).build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else {
            BasicResponse<Boolean> response = BasicResponse.<Boolean>builder().code(HttpStatus.OK.value()).httpStatus(HttpStatus.OK).message("SUCCESS").data(true).build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @Transactional()
    @PostMapping("/login")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "로그인", protocols = "http")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginDTO loginDTO) {

        User user = userRepo.getUserById(loginDTO.getLoginId()).orElseThrow(() -> new AccessDeniedException("Can't find member."));

        if (!loginDTO.getPassword().equals(user.getPassword())) {
            throw new AccessDeniedException("Wrong member!");
        }

        List<String> roles = new ArrayList<>();
        roles.add("ROLE_MEMBER");

        String accessToken = jwtTokenProvider.generateAccessToken(user, roles), refreshToken = jwtTokenProvider.generateRefreshToken(user, roles);

        Claims accessClaims  = jwtTokenProvider.getClaims(refreshToken), refreshClaims  = jwtTokenProvider.getClaims(refreshToken);

        AuthResponse response = AuthResponse.builder().accessToken(accessToken).refreshToken(refreshToken).createdAt(accessClaims.getIssuedAt()).build();

        RefreshToken newRefreshToken = RefreshToken.builder().user(user).refreshToken(refreshToken).createdAt(refreshClaims.getIssuedAt()).expiredAt(refreshClaims.getExpiration()).isExpired("N").build();
        refreshTokenRepository.save(newRefreshToken);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Transactional()
    @PostMapping("/logout")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "로그아웃", protocols = "http")
    public ResponseEntity<AuthResponse> login(@RequestBody LogoutDTO logoutDTO) {

        // User user = memberRepo.getMemberById(loginDTO.getId()).orElseThrow(() -> new ResourceNotFoundException("Can't find member."));

        /* if (!loginDTO.getPassword().equals(member.getPassword())) {
            throw new ForbiddenException("Wrong member!");
        } */

        refreshTokenRepository.logout(logoutDTO.getUserId());

        AuthResponse response = AuthResponse.builder().isLogout(true).build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @PostMapping("/renew")
    @ResponseStatus(value = HttpStatus.OK)
    @ApiOperation(value = "토큰 재발급", protocols = "http")
    public ResponseEntity<AuthResponse> renewAccessToken(@RequestBody RenewDTO renewDTO) {
        String refreshToken = renewDTO.getRefreshToken();

        try {
            jwtTokenProvider.validateToken(refreshToken);
        } catch (Exception ex) {
            throw new AccessDeniedException("Wrong refresh token.");
        }

        Integer userId = renewDTO.getUserId();

        RefreshToken latestRefreshToken = refreshTokenRepository.getLatestOne(userId).orElseThrow(() -> new AccessDeniedException("Empty refresh token."));

        if (!latestRefreshToken.getRefreshToken().equals(refreshToken)) {
            throw new AccessDeniedException("Not latest refresh token.");
        }

        User user = userRepo.findById(userId).orElseThrow(() -> new AccessDeniedException("Can't find user."));


        List<String> roles = new ArrayList<>();
        roles.add("ROLE_MEMBER");

        String accessToken = jwtTokenProvider.generateAccessToken(user, roles);

        Claims accessClaims  = jwtTokenProvider.getClaims(refreshToken);

        AuthResponse response = AuthResponse.builder().accessToken(accessToken).refreshToken(refreshToken).createdAt(accessClaims.getIssuedAt()).build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }



}
