package com.neroyun.mediator;

import com.neroyun.mediator.internal.Message;

/**
 * Represents an event that can be published to the mediator.
 * An event is a notification of something that has happened, and it typically does not expect a response.
 */
public interface Event extends Message<Void> {
}
