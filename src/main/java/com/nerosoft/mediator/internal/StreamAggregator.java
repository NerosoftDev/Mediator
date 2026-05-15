package com.nerosoft.mediator.internal;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toCollection;

/**
 * A utility class that provides a method to merge the elements of a stream in reverse order using a provided accumulator function.
 * @param <T> the type of elements in the stream
 */
class StreamAggregator<T> {
    private final Stream<T> stream;

    StreamAggregator(Stream<T> stream) {
        this.stream = stream;
    }

    /**
     * Merges the elements of the stream in reverse order using the provided accumulator function.
     * @param seed the initial value for the accumulation
     * @param accumulator a function that takes an element of the stream and the current accumulated value, and returns a new accumulated value
     * @return the result of merging the elements of the stream
     * @param <U> the type of the accumulated value
     */
    <U> U merge(U seed, BiFunction<? super T, U, U> accumulator) {
        Iterator<T> iterator = stream.collect(toCollection(LinkedList::new)).descendingIterator();
        U result = seed;
        while (iterator.hasNext()) {
            T element = iterator.next();
            result = accumulator.apply(element, result);
        }
        return result;
    }
}

