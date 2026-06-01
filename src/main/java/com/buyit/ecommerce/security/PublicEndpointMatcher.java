package com.buyit.ecommerce.security;

import com.buyit.ecommerce.anotations.Public;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;


@Component
public class PublicEndpointMatcher implements RequestMatcher {

    private final RequestMappingHandlerMapping handlerMapping;

    public PublicEndpointMatcher(
            @Qualifier("requestMappingHandlerMapping")
            RequestMappingHandlerMapping handlerMapping
    ) {
        this.handlerMapping = handlerMapping;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        try {
            HandlerExecutionChain chain = handlerMapping.getHandler(request);

            if (chain == null) {
                return false;
            }

            Object handler = chain.getHandler();

            if (handler instanceof HandlerMethod handlerMethod) {
                if (handlerMethod.getMethodAnnotation(Public.class) != null) {
                    return true;
                }

                if (AnnotatedElementUtils.hasAnnotation(handlerMethod.getBeanType(), Public.class)) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}