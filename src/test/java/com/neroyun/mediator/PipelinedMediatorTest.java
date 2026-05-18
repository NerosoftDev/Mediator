package com.neroyun.mediator;

import org.junit.jupiter.api.Test;

import java.util.concurrent.Executors;
import java.util.stream.Stream;

public class PipelinedMediatorTest {
    private final Mediator mediator;

    public PipelinedMediatorTest() {
        mediator = new PipelinedMediator()
                .use(() -> Stream.of(new UserCreateCommandHandler(), new UserCreatedEventHandler()))
                .use(() -> Stream.of(new UserCreateCommandValidator()))
                .use(() -> Stream.of(new LoggingMiddleware()))
                .use(() -> Executors.newFixedThreadPool(4));
    }

    @Test
    void testMediator() {
        mediator.send(new UserCreateCommand("John Doe", "johndoe@sample.com"));

        var users = UserStore.getInstance().getUsers();
        assert users.size() == 1;
        assert users.get(0).name().equals("John Doe");
    }

    @Test
    void testEventPublish() {
        // Arrange
        UserCreatedEvent event = new UserCreatedEvent(1234L, "Event Test User");

        // Act & Assert - should not throw exception
        mediator.publish(event);

        // Event publishing is asynchronous, so we just verify it doesn't throw
        assert true;
    }
}
