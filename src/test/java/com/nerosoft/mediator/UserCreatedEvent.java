package com.nerosoft.mediator;

public record UserCreatedEvent(Long id, String name) implements Event {
}
