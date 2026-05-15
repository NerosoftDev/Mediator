package com.neroyun.mediator.strategy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines the strategy for handling exceptions that occur in handlers within the mediator pattern.
 * This enum provides two strategies for handling exceptions:
 * 1. STOP: If an exception occurs in any handler, the processing of the message will be stopped immediately, and no further handlers will be invoked. This is useful when you want to ensure that if any handler fails, the entire processing of the message is halted to prevent inconsistent states or unintended side effects.
 * 2. CONTINUE: If an exception occurs in a handler, the processing will continue to the next handler in the chain. This allows other handlers to attempt to process the message, even if one handler fails. This can be useful in scenarios where you want to allow for partial processing of a message, or when you want to ensure that all handlers have a chance to process the message, regardless of any exceptions that may occur in individual handlers.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface HandlerExceptionStrategy {

    /**
     * Stops the processing of the message if an exception occurs in any handler.
     */
    String STOP = "STOP";

    /**
     * Continues to the next handler if an exception occurs in the current handler.
     * This allows other handlers to attempt to process the message, even if one handler fails.
     */
    String CONTINUE = "CONTINUE";

    String value() default CONTINUE;
}
