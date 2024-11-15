package org.example.expert.aop;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.example.expert.domain.common.annotation.Auth;
import org.example.expert.domain.log.dto.LogDto;
import org.example.expert.domain.log.service.LogService;
import org.example.expert.domain.user.entity.User;
import org.example.expert.security.UserDetailsImpl;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AdminAccessLoggingAspect {

    private final HttpServletRequest request;
    private final LogService logService;

    @Pointcut("execution(* org.example.expert.domain.manager.controller.ManagerController.saveManager(..))")
    private void saveManager() {}

    @Pointcut("execution(* org.example.expert.domain.todo.controller.TodoController.saveTodo(..))")
    private void saveTodo(){}

    @Before("execution(* org.example.expert.domain.user.controller.UserAdminController.changeUserRole(..))")
    public void logAfterChangeUserRole(JoinPoint joinPoint) {
        String userId = String.valueOf(request.getAttribute("userId"));
        String requestUrl = request.getRequestURI();
        LocalDateTime requestTime = LocalDateTime.now();

        log.info("Admin Access Log - User ID: {}, Request Time: {}, Request URL: {}, Method: {}",
                userId, requestTime, requestUrl, joinPoint.getSignature().getName());
    }

    @Before("saveManager() || saveTodo()")
    public void logBeforeManagerCreate(JoinPoint joinPoint) {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId =user.getUser().getId();
        String requestUrl = request.getRequestURI();
        String method = request.getMethod();
        LocalDateTime requestTime = LocalDateTime.now();

        log.info("Manager Create Log - User ID: {}, Request Time: {}, Request: {} {}, Method: {}",
                 userId, requestTime, method,requestUrl, joinPoint.getSignature().getName());

        LogDto logDto = LogDto.builder().userId(userId).method(method).requestUrl(requestUrl).requestTime(requestTime).build();
        logService.saveLog(logDto);
    }
}
