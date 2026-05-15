package com.neroyun.mediator;

import com.neroyun.mediator.internal.Message;
import com.neroyun.mediator.internal.MiddlewareDelegate;

public class LoggingMiddleware implements Middleware {

    @Override
    public Object handle(Message message, MiddlewareDelegate next) {
        System.out.println("LoggingMiddleware: Handling message of type " + message.getClass().getSimpleName());
        Object result = next.invoke();
        System.out.println("LoggingMiddleware: Finished handling message of type " + message.getClass().getSimpleName());
        return result;
    }
}
