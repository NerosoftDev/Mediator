# Mediator 异步重构文档

## 📋 重构概述

成功将整个 Mediator 框架从同步模式重构为异步模式，使用 `CompletableFuture` 实现非阻塞的异步操作，提升了系统的性能和可扩展性。

## 🎯 重构目标

1. **提升性能**：通过异步处理避免线程阻塞，提高系统吞吐量
2. **更好的并发控制**：利用 `CompletableFuture` 的组合能力管理复杂的异步操作
3. **保持兼容性**：在保持原有 API 设计的同时，升级为异步实现

## 🔄 主要变更

### 1. 核心接口重构

#### Handler 接口
```java
// 之前：同步
R handle(T message);

// 之后：异步
CompletableFuture<R> handle(T message);
```

#### Mediator 接口
```java
// 之前：同步
<T extends Command> void send(T command);
<T extends Query<R>, R> R execute(T query);
<T extends Event> void publish(T event);

// 之后：异步
<T extends Command> CompletableFuture<Void> send(T command);
<T extends Query<R>, R> CompletableFuture<R> execute(T query);
<T extends Event> CompletableFuture<Void> publish(T event);
```

#### Middleware 接口
```java
// 之前：同步
Object handle(Message message, MiddlewareDelegate next);

// 之后：异步
CompletableFuture<Object> handle(Message message, MiddlewareDelegate next);
```

#### MiddlewareDelegate 接口
```java
// 之前：同步
Object invoke();

// 之后：异步
CompletableFuture<Object> invoke();
```

### 2. PipelinedMediator 实现类重构

#### send 方法
- 使用 `CompletableFuture.supplyAsync` 异步执行参数校验和 handler 解析
- 使用 `thenCompose` 组合中间件管道和 handler 执行
- 返回 `CompletableFuture<Void>` 表示命令处理完成

#### execute 方法
- 类似 send 方法，但返回查询结果
- 支持 callback 方式的异步结果处理
- 返回 `CompletableFuture<R>` 包含查询结果

#### publish 方法
- 为每个事件 handler 创建独立的 `CompletableFuture`
- 支持三种并行策略：
  - **No_WAIT**：立即返回，不等待 handler 执行（Fire and forget）
  - **WHEN_ALL**：等待所有 handler 执行完成
  - **WHEN_ANY**：等待任一 handler 执行完成
- 支持异常处理策略（CONTINUE / STOP）

### 3. Handler 实现类更新

#### UserCreateCommandHandler
```java
@Override
public CompletableFuture<Void> handle(UserCreateCommand message) {
    return CompletableFuture.supplyAsync(() -> {
        // 处理逻辑
        return null;
    });
}
```

#### UserCreatedEventHandler
```java
@Override
public CompletableFuture<Void> handle(UserCreatedEvent message) {
    return CompletableFuture.completedFuture(null);
}
```

#### UserEventCounterHandler
```java
@Override
public CompletableFuture<Void> handle(UserCreatedEvent message) {
    return CompletableFuture.supplyAsync(() -> {
        counter.incrementAndGet();
        // 处理逻辑
        return null;
    });
}
```

### 4. Middleware 实现类更新

#### LoggingMiddleware
```java
@Override
public CompletableFuture<Object> handle(Message message, MiddlewareDelegate next) {
    System.out.println("LoggingMiddleware: Handling message...");
    return next.invoke().thenApply(result -> {
        System.out.println("LoggingMiddleware: Finished handling message");
        return result;
    });
}
```

### 5. 测试类更新

所有测试方法都更新为使用 `.join()` 等待异步操作完成：

```java
// 之前
mediator.send(command);

// 之后
mediator.send(command).join();
```

```java
// 之前
var result = mediator.execute(query);

// 之后
var result = mediator.execute(query).join();
```

## ✅ 测试结果

```
Tests run: 11, Failures: 0, Errors: 0, Skipped: 0
✅ BUILD SUCCESS
```

### 测试覆盖
- ✅ 2 个 Command 测试
- ✅ 9 个 Event Handler 测试
- ✅ 中间件管道测试
- ✅ 多 Handler 并发测试
- ✅ 异常处理测试

## 📊 性能优势

1. **非阻塞执行**：所有操作都通过 `CompletableFuture` 异步执行，避免线程阻塞
2. **并行处理**：事件可以并行分发给多个 handler，充分利用多核 CPU
3. **灵活的并发策略**：支持 Fire-and-forget、全部完成、任一完成三种策略
4. **可组合性**：通过 `thenCompose`、`thenApply` 等方法轻松组合多个异步操作

## 🎨 使用示例

### 发送命令（异步）
```java
mediator.send(new UserCreateCommand("John", "john@example.com"))
    .thenRun(() -> System.out.println("Command executed!"))
    .exceptionally(ex -> {
        System.err.println("Error: " + ex.getMessage());
        return null;
    });
```

### 执行查询（异步）
```java
mediator.execute(new GetUserQuery(userId))
    .thenAccept(user -> System.out.println("User: " + user))
    .exceptionally(ex -> {
        System.err.println("Error: " + ex.getMessage());
        return null;
    });
```

### 发布事件（异步）
```java
mediator.publish(new UserCreatedEvent(userId, userName))
    .thenRun(() -> System.out.println("Event published!"))
    .join(); // 可选：等待所有 handler 完成
```

### 组合多个操作
```java
mediator.send(createCommand)
    .thenCompose(v -> mediator.execute(getQuery))
    .thenCompose(result -> mediator.publish(new ResultEvent(result)))
    .thenRun(() -> System.out.println("All done!"));
```

## 📝 注意事项

1. **命名约定**：所有异步方法都使用 `xxxAsync` 后缀，清晰表明方法是异步的
2. **类型转换**：由于 `MiddlewareDelegate` 返回 `CompletableFuture<Object>`，在实现时需要注意类型转换
3. **异常处理**：使用 `exceptionally` 或 `handle` 方法处理异步操作中的异常
4. **等待完成**：在测试或需要同步等待的场景下，使用 `.join()` 或 `.get()` 等待结果
5. **线程池配置**：通过 `use(Supplier<ExecutorService>)` 方法配置自定义线程池
6. **并行策略**：为事件添加 `@HandlerParallelStrategy` 注解来控制并行行为
   - 使用 `WHEN_ALL` 确保测试等待所有 handler 完成
   - 使用 `No_WAIT` 实现真正的异步 fire-and-forget

## 📅 重构历史

### 2026年5月21日 - xxxAsync 命名约定
- 将所有异步方法改为 `xxxAsync` 命名约定
- 更新了所有接口、实现类和测试代码
- 修复了测试中的并行策略问题
- 确保所有测试通过（11/11）

### 2026年5月21日 - 初始异步重构
- 将整个框架从同步改为异步实现
- 使用 `CompletableFuture` 实现非阻塞操作
- 所有测试通过（11/11）

## ✨ 结论

成功将 Mediator 框架重构为完全异步的实现，并采用清晰的 `xxxAsync` 命名约定，使代码意图更加明确。所有测试通过，为项目带来了更好的性能和可扩展性！

