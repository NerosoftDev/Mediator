package com.neroyun.mediator.strategy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines the strategy for executing handlers in parallel within the mediator pattern.
 * This annotation can be applied to handler classes to specify how they should be executed in parallel when processing messages.
 * The available strategies include:
 * 1. NO_WAIT: Runs handlers in parallel without waiting for any of them to complete. This is useful for fire-and-forget scenarios where handlers can execute independently without blocking the main thread or waiting for their results.
 * 2. WHEN_ALL: Waits for all handlers to complete before proceeding. This is useful when you need to ensure that all handlers have finished processing before moving on to the next step in the workflow. The main thread will block until all handlers have completed their execution.
 * 3. WHEN_ANY: Waits for any handler to complete before proceeding. This is useful when you need to continue processing as soon as the first handler finishes, without waiting for all handlers to complete. The main thread will block until at least one handler has completed its execution.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface HandlerParallelStrategy {
    /**
     * Runs handlers in parallel without waiting for any of them to complete.
     * This strategy is useful when you want to fire-and-forget handlers,
     * allowing them to execute independently without blocking the main thread or waiting for their results.
     * Handlers will be executed concurrently, and the main thread will continue immediately after dispatching the handlers.
     */
    String No_WAIT = "NO_WAIT";

    /**
     * Waits for all handlers to complete before proceeding.
     * This strategy is useful when you need to ensure that all handlers have finished processing before moving on to the next step in the workflow.
     * The main thread will block until all handlers have completed their execution.
     */
    String WHEN_ALL = "WHEN_ALL";

    /**
     * Waits for any handler to complete before proceeding.
     * This strategy is useful when you need to continue processing as soon as the first handler finishes, without waiting for all handlers to complete.
     * The main thread will block until at least one handler has completed its execution.
     */
    String WHEN_ANY = "WHEN_ANY";

    String value() default No_WAIT;
}
