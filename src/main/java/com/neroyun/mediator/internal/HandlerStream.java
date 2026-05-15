package com.neroyun.mediator.internal;

import com.neroyun.mediator.Handler;

import java.util.stream.Stream;

/**
 * A functional interface that represents a supplier of a stream of handlers.
 * This interface is used to provide a stream of handlers to the mediator for processing commands.
 */
@FunctionalInterface
public interface HandlerStream {

    /**
     * Supplies a stream of handlers to the mediator for processing commands.
     *
     * @return a stream of handlers
     */
    @SuppressWarnings("rawtypes")
    Stream<Handler> supply();
}
