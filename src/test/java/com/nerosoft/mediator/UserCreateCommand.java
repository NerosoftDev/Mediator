package com.nerosoft.mediator;

public record UserCreateCommand(String name, String email) implements Command {
}
