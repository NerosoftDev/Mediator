package com.neroyun.mediator.internal;

@FunctionalInterface
public interface ExceptionHandle {
    void handleException(Throwable e);
}
