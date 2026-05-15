package com.nerosoft.mediator;

import com.nerosoft.mediator.internal.ExceptionHandle;

import static java.util.concurrent.CompletableFuture.runAsync;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

class Executor {
    static void run(List<Runnable> tasks, ExecutorService concurrentPolicy, ExceptionHandle onException) {
        try {
            tasks.forEach(task -> runAsync(task, concurrentPolicy));
        } catch (Throwable e) {
            onException.handleException(e);
        }
    }

    static void whenAll(List<Runnable> tasks, ExecutorService concurrentPolicy, ExceptionHandle onException) {
        CompletableFuture.allOf(tasks.stream()
                        .map(task -> {
                            return CompletableFuture.runAsync(task, concurrentPolicy)
                                    .exceptionally(ex -> {
                                        onException.handleException(ex);
                                        return null;
                                    });
                        })
                        .toArray(CompletableFuture[]::new))
                .join();
    }

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
