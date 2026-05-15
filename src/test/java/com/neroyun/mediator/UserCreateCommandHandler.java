package com.neroyun.mediator;

public class UserCreateCommandHandler implements Handler<UserCreateCommand, Void> {
    @Override
    public Void handle(UserCreateCommand message) {
        System.out.printf("UserCreateCommandHandler received command: %s\n", message);
        User user = new User(System.currentTimeMillis(), message.name(), message.email());
        UserStore.getInstance().addUser(user);
        System.out.printf("User created: %s\n", user);
        return null;
    }
}
