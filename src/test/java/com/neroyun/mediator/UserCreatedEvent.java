package com.neroyun.mediator;

public record UserCreatedEvent(Long id, String name) implements Event {
}
