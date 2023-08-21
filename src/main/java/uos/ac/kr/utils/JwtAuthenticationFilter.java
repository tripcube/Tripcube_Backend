package uos.ac.kr.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import uos.ac.kr.exceptions.AccessDeniedException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // 헤더에서 JWT 를 받아옵니다.
            // System.out.println("[JWTExceptionHandlerFilter] "+token.get());
            // 유효한 토큰인지 확인합니다.
            String URI = request.getRequestURI().toString();
            if (URI.contains("/auth") || URI.contains("/swagger-")) {
                filterChain.doFilter(request, response);
                return;
            }

            Optional<String> token = jwtTokenProvider.resolveToken((HttpServletRequest) request);
            if (token.isPresent() && jwtTokenProvider.validateToken(token.get())) {
                // 토큰이 유효하면 토큰으로부터 유저 정보를 받아옵니다.
                // System.out.println(token.get());
                Authentication authentication = jwtTokenProvider.getAuthentication(token.get());
                // SecurityContext 에 Authentication 객체를 저장합니다.
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
                return;
            }

            throw new RuntimeException();
        } catch (Exception ex) {
            //토큰에 오류가 있다면 401에러를 응답한다.
            System.out.println("[JWTExceptionHandlerFilter] " + ex.getMessage());
            request.setAttribute("exception", new AccessDeniedException(ex.getMessage()));
            filterChain.doFilter(request, response);
        }
    }

}