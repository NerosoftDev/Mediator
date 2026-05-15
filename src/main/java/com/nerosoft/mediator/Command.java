package com.nerosoft.mediator;

import com.nerosoft.mediator.internal.Message;
import com.nerosoft.mediator.internal.Validatable;

/**
 * Represents a command that can be sent to the mediator.
 * A command is an instruction to perform a specific action, and it typically does not expect a response.
 * Commands are used to change the state of the system or to trigger some behavior.
 */
public interface Command extends Message, Validatable {
}
