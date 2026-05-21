package com.neroyun.mediator;

import com.neroyun.mediator.internal.Generic;
import com.neroyun.mediator.internal.Message;

import java.util.concurrent.CompletableFuture;

/**
 * Defines a handler interface for processing messages of type T and producing a response of type R asynchronously.
 * This interface is a key component of the mediator pattern,
 * allowing for the decoupling of message senders and receivers by providing a common contract for handling messages.
 * Implementations of this interface will contain the logic to process specific types of messages and generate appropriate responses,
 * enabling a flexible and extensible architecture for handling various operations within the application.
 * @param <T> the type of message to be handled
 * @param <R> the type of response produced by the handler
 */
public interface Handler<T extends Message<R>, R> {
    /**
     * Handles the given message asynchronously and produces a response.
     * @param message the message to be processed by this handler
     * @return a CompletableFuture containing the response produced by handling the message
     */
    CompletableFuture<R> handleAsync(T message);

    /**
     * Determines if this handler can process the given message based on its type.
     * @param message the message to check
     * @return true if this handler can process the message, otherwise false
     */
    default boolean matches(T message) {
        Generic<T> generic = new Generic<>(getClass()) {
        };
        return generic.resolve().isAssignableFrom(message.getClass());
    }
}
