package com.neroyun.mediator;

public record UserCreateCommand(String name, String email) implements Command {
}
