package com.neroyun.mediator;

import com.neroyun.mediator.internal.ExceptionHandle;

import static java.util.concurrent.CompletableFuture.runAsync;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * Executor class for running tasks with different concurrency strategies.
 * This class provides methods to run tasks either immediately, wait for all tasks to complete, or wait for any task to complete.
 * It also handles exceptions using the provided ExceptionHandle.
 *
 */
class Executor {

    /**
     * Runs a list of tasks concurrently using the provided ExecutorService. If any task throws an exception, it will be handled by the onException handler.
     * @param tasks The list of tasks to be executed.
     * @param concurrentPolicy The ExecutorService to use for running the tasks.
     * @param onException The handler to use for any exceptions thrown by the tasks.
     */
    static void run(List<Runnable> tasks, ExecutorService concurrentPolicy, ExceptionHandle onException) {
        try {
            tasks.forEach(task -> runAsync(task, concurrentPolicy));
        } catch (Throwable e) {
            onException.handleException(e);
        }
    }

    /**
     * Runs a list of tasks concurrently and waits for all of them to complete. If any task throws an exception, it will be handled by the onException handler.
     * @param tasks The list of tasks to be executed.
     * @param concurrentPolicy The ExecutorService to use for running the tasks.
     * @param onException The handler to use for any exceptions thrown by the tasks.
     */
    static void whenAll(List<Runnable> tasks, ExecutorService concurrentPolicy, ExceptionHandle onException) {
        CompletableFuture.allOf(tasks.stream()
                        .map(task -> CompletableFuture.runAsync(task, concurrentPolicy)
                                .exceptionally(ex -> {
                                    onException.handleException(ex);
                                    return null;
                                }))
                        .toArray(CompletableFuture[]::new))
                .join();
    }

    /**
     * Runs a list of tasks concurrently and waits for any one of them to complete. If any task throws an exception, it will be handled by the onException handler.
     * @param tasks The list of tasks to be executed.
     * @param concurrentPolicy The ExecutorService to use for running the tasks.
     * @param onException The handler to use for any exceptions thrown by the tasks.
     */
    static void whenAny(List<Runnable> tasks, ExecutorService concurrentPolicy, ExceptionHandle onException) {
        List<CompletableFuture<Void>> futures = tasks.stream()
                .map(task -> CompletableFuture.runAsync(task, concurrentPolicy)
                        .exceptionally(ex -> {
                            onException.handleException(ex);
                            return null;
                        }))
                .toList();
        CompletableFuture.anyOf(futures.toArray(new CompletableFuture[]{})).join();
    }
}
