package com.neroyun.mediator.internal;

import com.neroyun.mediator.Middleware;

import java.util.stream.Stream;

/**
 * A functional interface that represents a supplier of a stream of middleware components.
 * This interface is used to provide a stream of middleware components to the mediator for processing commands.
 */
@FunctionalInterface
public interface MiddlewareStream {
    /**
     * Supplies a stream of middleware components to the mediator for processing commands.
     *
     * @return a stream of middleware components
     */
    Stream<Middleware> supply();
}
