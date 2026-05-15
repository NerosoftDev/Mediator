package com.nerosoft.mediator.internal;

import com.nerosoft.mediator.Validator;

import java.util.stream.Stream;

/**
 * A functional interface that represents a supplier of a stream of validators.
 * This interface is used to provide a stream of validators to the mediator for processing commands.
 */
@FunctionalInterface
public interface ValidatorStream {
    /**
     * Supplies a stream of validators to the mediator for processing commands.
     *
     * @return a stream of validators
     */
    @SuppressWarnings("rawtypes")
    Stream<Validator> supply();
}
