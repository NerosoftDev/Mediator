package com.neroyun.mediator;

import com.neroyun.mediator.internal.Message;
import com.neroyun.mediator.internal.MiddlewareDelegate;

/**
 * Represents a middleware that can be used in the mediator pipeline.
 * Middleware can be used to perform additional processing on messages before they are handled by their respective handlers.
 * This can include tasks such as logging, validation, authentication,
 * or any other cross-cutting concerns that you want to apply to messages as they pass through the mediator.
 * Middleware can be added to the mediator pipeline to intercept messages and perform actions before or after the main handling logic is executed.
 * This allows you to separate concerns and keep your handlers focused on their specific tasks, while still allowing for additional processing to be applied to messages in a consistent and reusable way.
 */
@FunctionalInterface
public interface Middleware {

    /**
     * Executes the middleware logic for the given message and then invokes the next middleware or handler in the chain.
     * @param message the message to be processed by the middleware
     * @param next the delegate to invoke the next middleware or handler in the chain
     * @return the result of the next middleware or handler
     */
    Object handle(Message message, MiddlewareDelegate next);
}
