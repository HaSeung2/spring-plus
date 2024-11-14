package org.example.expert.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.security.CustomUserDetailsService;
import org.example.expert.security.TokenNotValidateException;
import org.example.expert.security.UserDetailsImpl;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter implements Filter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public void doFilter(
        ServletRequest request,
        ServletResponse response,
        FilterChain chain){
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String bearerJwt = httpRequest.getHeader("Authorization");

        if (bearerJwt == null) {
            // 토큰이 없는 경우 400을 반환합니다.
            throw new TokenNotValidateException("JWT 토큰이 필요합니다.", HttpStatus.BAD_REQUEST);
        }

        String jwt = jwtUtil.substringToken(bearerJwt);

        try {
            // JWT 유효성 검사와 claims 추출
            Claims claims = jwtUtil.extractClaims(jwt);
            if (claims == null) {
                throw new TokenNotValidateException("잘못된 JWT 토큰입니다.", HttpStatus.BAD_REQUEST);
            }
            String username = claims.get("email", String.class);

            if (username != null && !username.isEmpty()
                && SecurityContextHolder.getContext().getAuthentication() == null) {
                SecurityContextHolder.getContext().setAuthentication(getUser(username));
            }

            chain.doFilter(request, response);
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.",
                      e);
            throw new TokenNotValidateException("유효하지 않는 JWT 서명입니다.",
                                                HttpStatus.UNAUTHORIZED);
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token, 만료된 JWT token 입니다.",
                      e);
            throw new TokenNotValidateException("만료된 JWT 토큰입니다.",
                                                HttpStatus.UNAUTHORIZED);
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.",
                      e);
            throw new TokenNotValidateException("만료된 JWT 토큰입니다.",
                                                HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Internal server error",
                      e);
            throw new TokenNotValidateException("지원되지 않는 JWT 토큰입니다.",
                                                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }

    private UsernamePasswordAuthenticationToken getUser(String username) {
        UserDetailsImpl user = (UserDetailsImpl) customUserDetailsService.loadUserByUsername(username);

        return new UsernamePasswordAuthenticationToken(user,
                                                       user.getPassword(),
                                                       Collections.singleton(new SimpleGrantedAuthority(user.getUser().getUserRole().name())));
    }
}
