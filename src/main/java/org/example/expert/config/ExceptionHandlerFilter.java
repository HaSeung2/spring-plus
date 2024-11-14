package org.example.expert.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.example.expert.security.ErrorMessageResponse;
import org.example.expert.security.TokenNotValidateException;
import org.springframework.stereotype.Component;

@Component
public class ExceptionHandlerFilter implements Filter {

    @Override
    public void doFilter(
        ServletRequest servletRequest,
        ServletResponse servletResponse,
        FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        try{
            filterChain.doFilter(request, response);
        }
        catch (TokenNotValidateException e){
            response.setStatus(e.getStatus().value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            String json = new ObjectMapper().writeValueAsString(new ErrorMessageResponse(e.getStatus().value(), e.getMessage()));
            response.getWriter().write(json);
        }
    }
}
