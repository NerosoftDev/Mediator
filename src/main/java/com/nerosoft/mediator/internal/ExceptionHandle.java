package com.nerosoft.mediator.internal;

@FunctionalInterface
public interface ExceptionHandle {
    void handleException(Throwable e);
}
