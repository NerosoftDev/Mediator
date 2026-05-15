package com.nerosoft.mediator;

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
    public void testMediator() {
        mediator.send(new UserCreateCommand("John Doe", "johndoe@sample.com"));

        var users = UserStore.getInstance().getUsers();
        assert users.size() == 1;
        assert users.get(0).name().equals("John Doe");
    }
}
