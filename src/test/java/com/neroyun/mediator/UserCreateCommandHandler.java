package com.neroyun.mediator;

import java.util.concurrent.CompletableFuture;

public class UserCreateCommandHandler implements Handler<UserCreateCommand, Void> {
    @Override
    public CompletableFuture<Void> handleAsync(UserCreateCommand message, MessageContext messageContext) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.printf("UserCreateCommandHandler received command: %s\n", message);
            User user = new User(System.currentTimeMillis(), message.name(), message.email());
            UserStore.getInstance().addUser(user);
            System.out.printf("User created: %s\n", user);
            return null;
        });
    }
}
