package com.buyit.ecommerce.config;

import com.buyit.ecommerce.exception.custom.DeniedAccessException;
import com.buyit.ecommerce.service.PermissionsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Collection;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthorizationInterceptor implements HandlerInterceptor {

    private final PermissionsService permissionService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){

        String mappedPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String method = request.getMethod();

        Collection<String> userRoles = (Collection<String>) request.getAttribute("USER_ROLES");

        if (userRoles == null || !permissionService.hasAccess(mappedPattern, method, userRoles)) {
            throw new DeniedAccessException("You do not have permission to access this resource.");
        }
        return true;
    }
}