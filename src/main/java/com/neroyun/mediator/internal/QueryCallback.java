package com.neroyun.mediator.internal;

@FunctionalInterface
public interface QueryCallback<R> {
    void onCompleted(R results);
}
