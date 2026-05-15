package com.neroyun.mediator;

import com.neroyun.mediator.internal.Message;
import com.neroyun.mediator.internal.Validatable;
import com.neroyun.mediator.validation.ValidationResult;

/**
 * Defines a contract for validating messages before they are processed by the mediator.
 * Implementations of this interface can be used to ensure that messages meet certain criteria or constraints before they are handled by the appropriate handlers in the mediator pattern.
 * @param <T> the type of message to be validated. Only messages that extend the Validatable class can be validated using this interface, ensuring that the validation logic is specific to the types of messages being processed in the mediator pattern.
 */
public interface Validator<T extends Validatable & Message<?>> {

    /**
     * Validates the given message and returns a ValidationResult indicating whether the validation was successful or if there were any errors.
     * @param message the message to be validated
     * @return the result of the validation, including any error messages if the validation failed
     */
    ValidationResult validate(T message);
}
