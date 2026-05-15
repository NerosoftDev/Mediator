package com.neroyun.mediator;

public class UserCreatedEventHandler implements Handler<UserCreatedEvent, Void> {

    @Override
    public Void handle(UserCreatedEvent message) {
        return null;
    }
}
