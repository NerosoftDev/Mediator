package com.neroyun.mediator;

import com.neroyun.mediator.strategy.HandlerParallelStrategy;

@HandlerParallelStrategy(HandlerParallelStrategy.WHEN_ALL)
public record UserCreatedEvent(Long id, String name) implements Event {
}
