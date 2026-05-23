package com.neroyun.mediator.internal;

@SuppressWarnings("unused")
public class NoHandlerRegisteredException extends RuntimeException {
    public NoHandlerRegisteredException(Class<?> messageType) {
        super("No handler registered for message type: " + messageType.getName() + ". ");
    }

    public NoHandlerRegisteredException(Class<?> messageType, String message) {
        super(String.format(message, messageType.getName()));
    }
}
