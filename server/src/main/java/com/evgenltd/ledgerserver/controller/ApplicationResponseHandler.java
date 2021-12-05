package com.evgenltd.ledgerserver.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class ApplicationResponseHandler implements ResponseBodyAdvice<Object> {

    private static final Logger log = LogManager.getLogger(ApplicationResponseHandler.class);

    @Override
    public boolean supports(
            @NotNull final MethodParameter returnType,
            @NotNull final Class<? extends HttpMessageConverter<?>> converterType
    ) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            final Object body,
            @NotNull final MethodParameter returnType,
            @NotNull final MediaType selectedContentType,
            @NotNull final Class<? extends HttpMessageConverter<?>> selectedConverterType,
            @NotNull final ServerHttpRequest request,
            @NotNull final ServerHttpResponse response
    ) {
        if (body instanceof Response) {
            return body;
        } else {
            return new Response<>(true, body, null);
        }
    }

    @ExceptionHandler(Throwable.class)
    public Response<?> handle(final Throwable throwable) {
        log.error("", throwable);
        return new Response<>(false, null, throwable.getMessage());
    }

    public static final record Response<T>(boolean success, T body, String error) {}
}
