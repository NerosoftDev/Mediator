# Mediator

一个轻量级的 Java Mediator 组件，用于在 CQRS 场景下统一处理 `Command`、`Query`、`Event`，并支持中间件管道、消息验证和事件并行策略。

## 项目简介

`Mediator` 通过“消息 + 处理器”的模式解耦业务调用方与实现方：

- `Command`：执行动作（通常无返回值）
- `Query<R>`：查询数据（有返回值）
- `Event`：发布通知（可被多个处理器订阅）

默认实现为 `PipelinedMediator`，核心特性：

- 自动匹配消息处理器（`Handler<T, R>`）
- 支持中间件链（`Middleware`）
- 支持验证器（`Validator<T>`），失败时抛出 `ValidationException`
- 支持事件并行分发策略（`HandlerParallelStrategy`）
- 支持事件异常处理策略（`HandlerExceptionStrategy`）

## 依赖与环境

项目为 Maven 工程（见 `pom.xml`）：

- `groupId`: `com.nerosoft`
- `artifactId`: `Mediator`
- `version`: `1.0.0`
- 测试依赖：`org.junit.jupiter:junit-jupiter:6.0.3`
- 编译版本：`maven.compiler.source/target = 25`

## 使用方法

### 1. 定义 Command 与 Handler

```java
public record UserCreateCommand(String name, String email) implements Command {}

public class UserCreateCommandHandler implements Handler<UserCreateCommand, Void> {
    @Override
    public Void handle(UserCreateCommand message) {
        System.out.println("create user: " + message.email());
        return null;
    }
}
```

### 2. （可选）定义 Validator

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

### 3. 组装 Mediator

```java
Mediator mediator = new PipelinedMediator()
        .use(() -> java.util.stream.Stream.of(new UserCreateCommandHandler()))
        .use(() -> java.util.stream.Stream.of(new UserCreateCommandValidator()))
        .use(() -> java.util.stream.Stream.of(
                (message, next) -> {
                    long start = System.nanoTime();
                    try {
                        return next.invoke();
                    } finally {
                        long cost = System.nanoTime() - start;
                        System.out.println("handled " + message.getClass().getSimpleName() + " in " + cost + " ns");
                    }
                }
        ));
```

### 4. 发送消息

```java
mediator.send(new UserCreateCommand("Alice", "alice@example.com"));
```

如校验失败，会抛出 `ValidationException`，可通过 `getErrors()` 读取错误列表。

## Spring Boot 集成方法

本项目本身不依赖 Spring；推荐在你的 Spring Boot 业务工程中引入该库后，通过 `@Configuration` 装配 `Mediator`。

### 1. 在业务工程引入依赖

如果你已将该库发布到私有仓库或本地仓库，可在业务工程 `pom.xml` 中添加：

```xml
<dependency>
    <groupId>com.nerosoft</groupId>
    <artifactId>Mediator</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 将 Handler / Validator / Middleware 交给 Spring 管理

```java
@Component
public class UserCreateCommandHandler implements Handler<UserCreateCommand, Void> {
    @Override
    public Void handle(UserCreateCommand message) {
        return null;
    }
}

@Component
public class UserCreateCommandValidator implements Validator<UserCreateCommand> {
    @Override
    public ValidationResult validate(UserCreateCommand message) {
        return ValidationResult.success();
    }
}
```

### 3. 在配置类中组装 `PipelinedMediator`

```java
import com.nerosoft.mediator.*;
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

### 4. 在业务服务中使用

```java
import com.nerosoft.mediator.Mediator;
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

说明：

- `Handler` 默认按“消息泛型类型”匹配
- 多个 `Middleware` 会按流顺序组成责任链
- `Validator` 返回失败时会抛出 `ValidationException`，可统一在全局异常处理中转换为 HTTP 响应

## Event 并行策略（可选）

给事件类型添加注解控制并发行为：

```java
@HandlerParallelStrategy(HandlerParallelStrategy.WHEN_ALL)
@HandlerExceptionStrategy(HandlerExceptionStrategy.CONTINUE)
public class UserCreatedEvent implements Event {}
```

- `NO_WAIT`：派发后不等待
- `WHEN_ALL`：等待全部处理器完成
- `WHEN_ANY`：任一处理器完成即继续
- `STOP`：任一处理器异常立即终止
- `CONTINUE`：收集异常，最后统一抛出

## 包内容

- `com.nerosoft.mediator`
  - 核心抽象：`Mediator`、`Command`、`Query`、`Event`
  - 扩展点：`Handler`、`Middleware`、`Validator`
  - 默认实现：`PipelinedMediator`
- `com.nerosoft.mediator.strategy`
  - 事件并行与异常策略注解
- `com.nerosoft.mediator.validation`
  - `ValidationResult`、`ValidationException`
- `com.nerosoft.mediator.internal`
  - 内部支持类型（消息基类、流供应器、异常聚合等）

## 快速构建

```bash
mvn clean test
```

如果本地 JDK 版本与 `pom.xml` 不一致，请先调整 JDK 或修改 `maven.compiler.source/target`。
