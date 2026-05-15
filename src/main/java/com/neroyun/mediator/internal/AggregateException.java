package com.neroyun.mediator.internal;

import java.util.List;

public class AggregateException extends RuntimeException {

    private final List<Throwable> exceptions;

    public AggregateException(List<Throwable> exceptions) {
        super("Multiple exceptions occurred during handler execution.");
        this.exceptions = exceptions;
    }

    public List<Throwable> getExceptions() {
        return exceptions;
    }
}
