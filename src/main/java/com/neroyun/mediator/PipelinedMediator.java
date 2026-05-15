package com.neroyun.mediator;

import com.neroyun.mediator.internal.*;
import com.neroyun.mediator.internal.*;
import com.neroyun.mediator.strategy.HandlerExceptionStrategy;
import com.neroyun.mediator.strategy.HandlerParallelStrategy;
import com.neroyun.mediator.validation.ValidationException;
import com.neroyun.mediator.validation.ValidationResult;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * This class is an implementation of the Mediator interface that provides a pipelined approach to handling commands, queries, and events.
 * It allows for the processing of commands, queries, and events in a sequential manner, where each command, query,
 * or event is processed one at a time, and the next one is not processed until the current one is completed.
 * This can be useful in scenarios where the order of processing is important,
 * or when there are dependencies between commands, queries, and events that need to be respected.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class PipelinedMediator implements Mediator {
    private StreamSupplier<Handler> handlers = Stream::empty;
    private StreamSupplier<Middleware> middlewares = Stream::empty;
    private StreamSupplier<Validator> validators = Stream::empty;
    private Supplier<ExecutorService> concurrentPolicy = Executors::newCachedThreadPool;

    /**
     * Configures the mediator to use the provided stream of handlers for processing commands, queries, and events.
     *
     * @param handlers the stream of handlers to be used by the mediator
     * @return the current instance of PipelinedMediator for method chaining
     */
    public PipelinedMediator use(HandlerStream handlers) {
        this.handlers = handlers::supply;
        return this;
    }

    public PipelinedMediator use(MiddlewareStream middlewares) {
        this.middlewares = middlewares::supply;
        return this;
    }

    public PipelinedMediator use(ValidatorStream validators) {
        this.validators = validators::supply;
        return this;
    }

    public PipelinedMediator use(Supplier<ExecutorService> concurrentPolicy) {
        this.concurrentPolicy = concurrentPolicy;
        return this;
    }

    @Override
    public <T extends Command> void send(T command) {
        checkArguments(command, "Command can not be null.");
        validate(command);
        var handler = resolveHandler(command);
        var pipeline = buildMiddlewarePipeline(command, () -> handler.handle(command));
        pipeline.invoke();
    }

    @Override
    public <T extends Query<R>, R> R execute(T query) {
        checkArguments(query, "Query can not be null.");
        validate(query);
        var handler = resolveHandler(query);
        var pipeline = buildMiddlewarePipeline(query, () -> handler.handle(query));
        return (R) pipeline.invoke();
    }

    @Override
    public <T extends Query<R>, R> void execute(T query, R response) {
        checkArguments(query, "Query can not be null.");
    }

    @Override
    public <T extends Event> void publish(T event) {
        checkArguments(event, "Event can not be null.");

        List<Runnable> tasks = handlers.supply()
                .filter(handler -> handler.matches(event))
                .map(handler -> (Handler<Event, Void>) handler)
                .map(handler -> (Runnable) () -> {
                    var pipeline = buildMiddlewarePipeline(event, () -> handler.handle(event));
                    pipeline.invoke();
                })
                .toList();

        if (tasks.isEmpty()) {
            return;
        }

        HandlerParallelStrategy parallelStrategy = event.getClass().getAnnotation(HandlerParallelStrategy.class);
        HandlerExceptionStrategy exceptionStrategy = event.getClass().getAnnotation(HandlerExceptionStrategy.class);

        var parallelStrategyValue = parallelStrategy != null ? parallelStrategy.value() : HandlerParallelStrategy.No_WAIT;
        var exceptionStrategyValue = exceptionStrategy != null ? exceptionStrategy.value() : HandlerExceptionStrategy.CONTINUE;

        List<Throwable> exceptions = new java.util.ArrayList<>();

        ExceptionHandle exceptionHandle = exception -> {
            if (Objects.equals(exceptionStrategyValue, HandlerExceptionStrategy.STOP)) {
                throw new RuntimeException(exception);
            } else {
                exceptions.add(exception);
            }
        };

        switch (parallelStrategyValue) {
            case HandlerParallelStrategy.No_WAIT -> Executor.run(tasks, concurrentPolicy.get(), exceptionHandle);
            case HandlerParallelStrategy.WHEN_ALL -> Executor.whenAll(tasks, concurrentPolicy.get(), exceptionHandle);
            case HandlerParallelStrategy.WHEN_ANY -> Executor.whenAny(tasks, concurrentPolicy.get(), exceptionHandle);
        }

        if (!exceptions.isEmpty()) {
            throw new AggregateException(exceptions);
        }
    }

    /**
     * Resolves the appropriate handler for the given message by filtering through the stream of handlers and finding the first one that matches the message type.
     * If no matching handler is found, it throws a RuntimeException indicating that no handler was found for the message.
     * This method is crucial for ensuring that messages are processed by the correct handlers based on their types,
     * allowing for a flexible and extensible architecture in the mediator pattern.
     *
     * @param message the message for which a handler is to be resolved
     * @param <T>     the type of the message
     * @param <R>     the type of the response produced by the message handler
     * @return the resolved handler for the given message
     */
    private <T extends Message<R>, R> Handler<T, R> resolveHandler(T message) {
        // resolve handler from handlers stream
        return handlers.supply()
                .filter(handler -> handler.matches(message))
                .map(handler -> (Handler<T, R>) handler)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No handler found for message: " + message.getClass().getName()));
    }

    /**
     * Validates the given message using the available validators. It iterates through the stream of validators,
     * applies each validator to the message, and collects any validation errors. If there are any validation errors, it throws a ValidationException containing the list of errors. This method ensures that messages are validated before they are processed by the handlers, allowing for better error handling and improved code readability when dealing with validation logic in the mediator pattern.
     *
     * @param message the message to be validated
     * @param <T>     the type of the message
     * @param <R>     the type of the response produced by the message handler
     */
    private <T extends Validatable & Message<R>, R> void validate(T message) {
        var errors = validators.supply()
                .map(validator -> validator.validate(message))
                .filter(ValidationResult::isFailure)
                .flatMap(result -> result.getErrors().stream())
                .toList();
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    /**
     * Checks if the provided argument is not null.
     * If the argument is null, it throws an IllegalArgumentException with the provided message.
     *
     * @param argument the argument to be checked for nullity
     * @param message  the exception message to be used if the argument is null
     */
    private void checkArguments(Object argument, String message) {
        if (argument != null) {
            return;
        }

        throw new IllegalArgumentException(message != null ? message : "Argument can not be null.");
    }

    /**
     * Builds a middleware pipeline for the given message and final action.
     * The pipeline is constructed by wrapping the final action with each applicable middleware in reverse order,
     * allowing each middleware to process the message before and/or after the final action is invoked.
     *
     * @param message     the message to be processed by the middleware pipeline
     * @param finalAction the final action to be executed after all middlewares have been applied
     * @param <T>         the type of the message
     * @param <R>         the type of the response produced by the message handler
     * @return a delegate representing the complete middleware pipeline
     */
    private <T extends Message<R>, R> MiddlewareDelegate buildMiddlewarePipeline(T message, MiddlewareDelegate finalAction) {
        var applicableMiddlewares = middlewares.supply().toList();
        MiddlewareDelegate delegate = finalAction;
        for (int i = applicableMiddlewares.size() - 1; i >= 0; i--) {
            Middleware middleware = applicableMiddlewares.get(i);
            MiddlewareDelegate next = delegate;
            delegate = () -> middleware.handle(message, next);
        }
        return delegate;
    }
}
