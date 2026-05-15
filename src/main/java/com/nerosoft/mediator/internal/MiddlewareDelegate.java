package com.nerosoft.mediator.internal;

/**
 * The next invocation of the middleware chain.
 * To invoke the next middleware or handler in the chain, call the invoke() method on this delegate.
 * This delegate is passed to each middleware and handler in the chain, allowing them to control when the next middleware is invoked.
 * Middleware and handlers can choose to invoke the next middleware immediately,
 * or they can perform some processing before invoking the next middleware.
 * This allows for flexible control over the flow of the middleware chain,
 * enabling middleware to perform tasks such as logging, validation, authentication, or any other cross-cutting concerns before the main handling logic is executed.
 * By using this delegate, middleware and handlers can ensure that the next middleware in the chain is invoked at the appropriate time,
 * allowing for a consistent and reusable way to apply additional processing to messages as they pass through the mediator.
 */
@FunctionalInterface
public interface MiddlewareDelegate {
    /**
     * Invokes the next middleware or handler in the chain.
     * @return the result of the next middleware or handler
     */
    Object invoke();
}
