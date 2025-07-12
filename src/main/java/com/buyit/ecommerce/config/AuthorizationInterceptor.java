package com.buyit.ecommerce.config;

import com.buyit.ecommerce.exception.custom.DeniedAccessException;
import com.buyit.ecommerce.service.PermissionsService;
import io.micrometer.core.instrument.MeterRegistry;
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
    private final MeterRegistry meterRegistry;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String mappedPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String method = request.getMethod();

        Boolean isPublic = (Boolean) request.getAttribute("IS_PUBLIC_ENDPOINT");
        if (Boolean.TRUE.equals(isPublic)) {
            meterRegistry.counter("ecommerce_access_public", "method", method, "path", mappedPattern).increment();
            return true;
        }

        Collection<String> userRoles = (Collection<String>) request.getAttribute("USER_ROLES");

        if (userRoles == null || !permissionService.hasAccess(mappedPattern, method, userRoles)) {
            meterRegistry.counter("ecommerce_access_denied", "method", method, "path", mappedPattern).increment();
            throw new DeniedAccessException("You do not have permission to access this resource.");
        }
        meterRegistry.counter("ecommerce_access_granted", "method", method, "path", mappedPattern).increment();
        return true;
    }
}