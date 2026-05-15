package com.nerosoft.mediator;

import com.nerosoft.mediator.internal.Message;
import com.nerosoft.mediator.internal.Validatable;

/**
 * Represents a query that can be sent to the mediator.
 * A query is a request for data or information, and it typically expects a response of type R.
 *
 * @param <R> the type of the response expected from the query.
 */
public interface Query<R> extends Message<R>, Validatable {
}
