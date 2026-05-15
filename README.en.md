# Mediator

A lightweight Java Mediator library for CQRS scenarios, supporting unified handling of `Command`, `Query`, and `Event` with middleware pipelines, message validation, and event parallel dispatch strategies.

[![Maven Central](https://img.shields.io/maven-central/v/com.neroyun/mediator)](https://central.sonatype.com/artifact/com.neroyun/mediator)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://github.com/NerosoftDev/Mediator/blob/master/LICENSE)

## Overview

`Mediator` decouples message senders from their handlers using a message-driven approach:

- **`Command`** — Triggers an action (typically no return value)
- **`Query<R>`** — Requests data and returns a result of type `R`
- **`Event`** — Publishes a notification that can be handled by multiple subscribers

The default implementation is `PipelinedMediator`, which provides:

- Automatic handler resolution by message type (`Handler<T, R>`)
- Middleware pipeline support (`Middleware`)
- Message validation (`Validator<T>`), throwing `ValidationException` on failure
- Event parallel dispatch strategies (`HandlerParallelStrategy`)
- Event exception handling strategies (`HandlerExceptionStrategy`)

## Requirements

- Java 17+
- Maven

## Installation

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.neroyun</groupId>
    <artifactId>mediator</artifactId>
    <version>${VERSION}</version>
</dependency>
```

## Quick Start

### 1. Define a Command and its Handler

```java
public record UserCreateCommand(String name, String email) implements Command {}

public class UserCreateCommandHandler implements Handler<UserCreateCommand, Void> {
    @Override
    public Void handle(UserCreateCommand message) {
        System.out.println("Creating user: " + message.email());
        return null;
    }
}
```

### 2. (Optional) Define a Validator

```java
public class UserCreateCommandValidator implements Validator<UserCreateCommand> {
    @Override
    public ValidationResult validate(UserCreateCommand message) {
        if (message.name() == null || message.name().isBlank()) {
            return ValidationResult.failure("Name is required");
        }
        if (message.email() == null || !message.email().contains("@")) {
            return ValidationResult.failure("Email is invalid");
        }
        return ValidationResult.success();
    }
}
```

### 3. Assemble the Mediator

```java
Mediator mediator = new PipelinedMediator()
        .use(() -> Stream.of(new UserCreateCommandHandler()))
        .use(() -> Stream.of(new UserCreateCommandValidator()))
        .use(() -> Stream.of(
                (message, next) -> {
                    long start = System.nanoTime();
                    try {
                        return next.invoke();
                    } finally {
                        long cost = System.nanoTime() - start;
                        System.out.println("Handled " + message.getClass().getSimpleName() + " in " + cost + " ns");
                    }
                }
        ));
```

### 4. Send a Message

```java
mediator.send(new UserCreateCommand("Alice", "alice@example.com"));
```

If validation fails, a `ValidationException` is thrown. Use `getErrors()` to retrieve the list of error messages.

---

## Middleware

`Middleware` intercepts messages before or after they reach a `Handler`. Typical use cases include logging, performance monitoring, authentication, auditing, and distributed tracing.

### Middleware Interface

`Middleware` is a `@FunctionalInterface`:

```java
@FunctionalInterface
public interface Middleware {
    Object handle(Message message, MiddlewareDelegate next);
}
```

- `message` — The message currently being processed
- `next` — Invokes the next middleware or the final handler in the chain

### Registering Middleware

Pass middleware via `.use(() -> Stream.of(...))` when building `PipelinedMediator`:

```java
Mediator mediator = new PipelinedMediator()
        .use(() -> Stream.of(new UserCreateCommandHandler()))
        .use(() -> Stream.of(new UserCreateCommandValidator()))
        .use(() -> Stream.of(
                (message, next) -> {
                    System.out.println("Before: " + message.getClass().getSimpleName());
                    try {
                        return next.invoke();
                    } finally {
                        System.out.println("After: " + message.getClass().getSimpleName());
                    }
                }
        ));
```

### Execution Order

Middleware forms a chain in registration order:

1. The first registered middleware executes first
2. Calling `next.invoke()` passes control to the next middleware
3. Finally, the matching `Handler` is invoked
4. After the handler returns, each middleware continues its post-processing in reverse order

### Common Patterns

#### Logging and Timing

```java
(message, next) -> {
    long start = System.nanoTime();
    try {
        return next.invoke();
    } finally {
        System.out.println("Elapsed (ns): " + (System.nanoTime() - start));
    }
}
```

#### Pre-condition / Authorization Check

```java
(message, next) -> {
    if (message == null) {
        throw new IllegalArgumentException("Message must not be null");
    }
    return next.invoke();
}
```

---

## Event Parallel Dispatch Strategies

Annotate your event class to control how its handlers are dispatched:

```java
@HandlerParallelStrategy(HandlerParallelStrategy.WHEN_ALL)
@HandlerExceptionStrategy(HandlerExceptionStrategy.CONTINUE)
public class UserCreatedEvent implements Event {}
```

### `@HandlerParallelStrategy`

| Value | Description |
|-------|-------------|
| `NO_WAIT` *(default)* | Dispatches handlers asynchronously without waiting for completion (fire-and-forget) |
| `WHEN_ALL` | Waits for all handlers to complete before returning |
| `WHEN_ANY` | Waits until any one handler completes, then continues |

### `@HandlerExceptionStrategy`

| Value | Description |
|-------|-------------|
| `CONTINUE` *(default)* | Collects exceptions from all handlers and throws an `AggregateException` at the end |
| `STOP` | Stops processing immediately when any handler throws an exception |

---

## Spring Boot Integration

This library has no dependency on Spring. To integrate it into a Spring Boot application, wire a `PipelinedMediator` bean in a `@Configuration` class.

### Register Handlers, Validators, and Middlewares as Spring Beans

```java
@Component
public class UserCreateCommandHandler implements Handler<UserCreateCommand, Void> {
    @Override
    public Void handle(UserCreateCommand message) {
        // business logic
        return null;
    }
}

@Component
public class UserCreateCommandValidator implements Validator<UserCreateCommand> {
    @Override
    public ValidationResult validate(UserCreateCommand message) {
        if (message.name() == null || message.name().isBlank()) {
            return ValidationResult.failure("Name is required");
        }
        return ValidationResult.success();
    }
}
```

### Assemble the Mediator Bean

```java
import com.neroyun.mediator.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.Executors;

@Configuration
public class MediatorConfiguration {

    @Bean
    public Mediator mediator(ApplicationContext applicationContext) {
        return new PipelinedMediator()
                .use(() -> applicationContext.getBeansOfType(Handler.class).values().stream())
                .use(() -> applicationContext.getBeansOfType(Validator.class).values().stream())
                .use(() -> applicationContext.getBeansOfType(Middleware.class).values().stream())
                .use(() -> Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
    }
}
```

### Inject into a Service

```java
import com.neroyun.mediator.Mediator;
import org.springframework.stereotype.Service;

@Service
public class UserApplicationService {
    private final Mediator mediator;

    public UserApplicationService(Mediator mediator) {
        this.mediator = mediator;
    }

    public void createUser(String name, String email) {
        mediator.send(new UserCreateCommand(name, email));
    }
}
```

> **Notes:**
> - Handlers are matched automatically by their generic message type
> - Multiple middlewares form a chain in stream order
> - When a `Validator` returns a failure, a `ValidationException` is thrown — catch it in a global exception handler (e.g., `@ControllerAdvice`) to return a proper HTTP error response

---

## Package Structure

| Package | Contents |
|---------|----------|
| `com.neroyun.mediator` | Core abstractions: `Mediator`, `Command`, `Query`, `Event`; extension points: `Handler`, `Middleware`, `Validator`; default implementation: `PipelinedMediator` |
| `com.neroyun.mediator.strategy` | Event parallel and exception strategy annotations |
| `com.neroyun.mediator.validation` | `ValidationResult`, `ValidationException` |
| `com.neroyun.mediator.internal` | Internal support types (message base, stream suppliers, exception aggregation, etc.) |

---

## Building

```bash
mvn clean test
```

Ensure your local JDK version matches the `maven.compiler.release` setting in `pom.xml` (currently Java 17).

---

## License

This project is licensed under the [GNU General Public License v3.0](https://github.com/NerosoftDev/Mediator/blob/master/LICENSE).
