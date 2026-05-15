package com.nerosoft.mediator;

public class PipelinedMediator implements Mediator {
    @Override
    public <T extends Command> void send(T command) {

    }

    @Override
    public <T extends Query<R>, R> R execute(T query) {
        return null;
    }

    @Override
    public <T extends Query<R>, R> void execute(T query, R response) {

    }

    @Override
    public <T extends Event> void publish(T event) {

    }
}
