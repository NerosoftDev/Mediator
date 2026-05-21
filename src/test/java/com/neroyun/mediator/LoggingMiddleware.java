package com.neroyun.mediator;

import com.neroyun.mediator.internal.Message;
import com.neroyun.mediator.internal.MiddlewareDelegate;

import java.util.concurrent.CompletableFuture;

public class LoggingMiddleware implements Middleware {

    @SuppressWarnings("rawtypes")
    @Override
    public CompletableFuture<Object> handleAsync(Message message, MiddlewareDelegate next) {
        System.out.println("LoggingMiddleware: Handling message of type " + message.getClass().getSimpleName());
        return next.invokeAsync().thenApply(result -> {
            System.out.println("LoggingMiddleware: Finished handling message of type " + message.getClass().getSimpleName());
            return result;
        });
    }
}
