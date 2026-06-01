package com.neroyun.mediator;

import java.util.concurrent.CompletableFuture;

public class UserCreatedEventHandler implements Handler<UserCreatedEvent, Void> {

    @Override
    public CompletableFuture<Void> handleAsync(UserCreatedEvent message, MessageContext messageContext) {
        return CompletableFuture.completedFuture(null);
    }
}
