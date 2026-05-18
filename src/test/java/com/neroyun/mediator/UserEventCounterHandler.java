package com.neroyun.mediator;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A test event handler that counts how many UserCreatedEvent instances it has handled.
 * Uses AtomicInteger for thread-safe counting.
 */
public class UserEventCounterHandler implements Handler<UserCreatedEvent, Void> {
    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public Void handle(UserCreatedEvent message) {
        counter.incrementAndGet();
        System.out.printf("UserEventCounterHandler: Counted event for user %s (Total: %d)\n",
                         message.name(), counter.get());
        return null;
    }

    public int getCount() {
        return counter.get();
    }

    public void reset() {
        counter.set(0);
    }
}

