package uos.ac.kr.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import uos.ac.kr.domains.CustomUserDetails;
import uos.ac.kr.domains.User;
import uos.ac.kr.repositories.UserRepository;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    private long accessTokenValidTime = 60 * 60 * 1000L; // access token 유효시간 1시간
    private long refreshTokenValidTime = 2 * 60 * 60 * 1000L; // refresh token 유효시간 24시간

    private final UserRepository memberRepo;

    // 객체 초기화, secretKey를 Base64로 인코딩한다.
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // JWT access token 생성
    public String generateAccessToken(User user, List<String> roles) {
        Claims claims = Jwts.claims().setSubject("MEMBER_TOKEN").setIssuer("Math"); // JWT payload 에 저장되는 정보단위

        claims.put("userId", user.getUserId());
        claims.put("loginId", user.getLoginId());
        claims.put("roles", roles); // 정보는 key / value 쌍으로 저장된다.

        Date now = new Date();
        return Jwts.builder().setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + accessTokenValidTime)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, secretKey) // 사용할 암호화 알고리즘과 signature 에 들어갈 secret값 세팅
                .compact();
    }

    // JWT refresh token 생성
    public String generateRefreshToken(User user, List<String> roles) {
        Claims claims = Jwts.claims().setSubject("MEMBER_TOKEN").setIssuer("Math"); // JWT payload 에 저장되는 정보단위

        claims.put("userId", user.getUserId());
        claims.put("loginId", user.getLoginId());
        claims.put("roles", roles);

        Date now = new Date();
        return Jwts.builder().setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + refreshTokenValidTime)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, secretKey) // 사용할 암호화 알고리즘과 signature 에 들어갈 secret값 세팅
                .compact();
    }

    // JWT 토큰에서 인증 정보 조회
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);

        int userId = Integer.parseInt(claims.get("userId").toString());
        String loginId = claims.get("loginId").toString();

        @SuppressWarnings("unchecked")
        List<String> roles = (ArrayList<String>) claims.get("roles");

        CustomUserDetails userDetails = CustomUserDetails.builder().userId(userId).loginId(loginId).roles(roles).build();

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에서 회원 정보 추출
    public Claims getClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    // Request의 Header에서 token 값을 가져옵니다. "X-AUTH-TOKEN" : "TOKEN값'
    public Optional<String> resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (!bearerToken.isEmpty() && !bearerToken.isBlank()) {
            if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
                return Optional.of(bearerToken.substring(7));
            }
            return Optional.of(bearerToken);
        }
        return Optional.empty();
    }

    // 토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken) {
       // try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        /* } catch (Exception e) {
            return false;
        } */
    }

    // 토큰의 유효성 + 만료일자 + refreshToken의 유효성 확인
    /* @Transactional(readOnly = true)
    public boolean validateAccessToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);

            int num = Integer.parseInt(getClaims(jwtToken).get("num").toString());
            String refreshToken = memberRepo.getById(num).getRefreshToken();
            if (refreshToken.isEmpty())
                throw new AccessDeniedException("The user is logged out");
            if (!validateToken(refreshToken))
                throw new AccessDeniedException("The refreshToken has expired");

            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    // 유효한 토큰이면서 시간만 만료되어야 true, 정상이거나 문제가 있으면 false
    public boolean validateTokenExceptExpiration(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return claims.getBody().getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            return false;
        }
    } */
}
