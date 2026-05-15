package com.neroyun.mediator.internal;

import java.util.stream.Stream;

@FunctionalInterface
public interface StreamSupplier<T> {
    Stream<T> supply();

    /**
     * Creates a StreamAggregator that can be used to aggregate the elements of the stream supplied by this StreamSupplier.
     * @return a StreamAggregator for the elements of the stream
     */
    default StreamAggregator<T> aggregate() {
        return new StreamAggregator<>(supply());
    }
}
