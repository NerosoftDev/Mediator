package com.neroyun.mediator;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class PipelinedMediatorTest {
    private final Mediator mediator;

    public PipelinedMediatorTest() {
        mediator = new PipelinedMediator()
                .use(() -> Stream.of(new UserCreateCommandHandler(), new UserCreatedEventHandler()))
                .use(() -> Stream.of(new UserCreateCommandValidator()))
                .use(() -> Stream.of(new LoggingMiddleware()))
                .use(event-> CompletableFuture.completedFuture(null));
    }

    @Test
    void testMediator() {
        // Wait for async command to complete
        mediator.sendAsync(new UserCreateCommand("John Doe", "johndoe@sample.com")).join();

        var users = UserStore.getInstance().getUsers();
        assert users.size() == 1;
        assert users.get(0).name().equals("John Doe");
    }

    @Test
    void testEventPublish() {
        // Arrange
        UserCreatedEvent event = new UserCreatedEvent(1234L, "Event Test User");

        // Act - wait for async event publishing to complete
        mediator.publishAsync(event).join();

        // Assert - event publishing completed without throwing
        assert true;
    }
}
