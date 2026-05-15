package com.nerosoft.mediator.validation;

import java.util.List;

/**
 * Represents an exception that is thrown when validation fails in the mediator pattern.
 * This exception encapsulates the details of the validation failure,
 * including a list of error messages that describe the reasons for the failure.
 * The ValidationException class extends RuntimeException,
 * allowing it to be thrown without the need for explicit handling in the code that performs validation.
 * By providing a dedicated exception for validation failures,
 * it allows for better error handling and improved code readability when dealing with validation logic in the mediator pattern.
 * The ValidationException class also provides a method to retrieve the ValidationResult associated with the exception,
 * allowing for easy access to the details of the validation failure when catching the exception.
 * Overall, this class serves as a clear and consistent way to represent validation failures in the mediator pattern,
 * promoting better error handling and improved code readability when dealing with validation logic in the application.
 */
public class ValidationException extends RuntimeException {
    private final transient ValidationResult result;

    /**
     * Creates a new ValidationException with the specified list of error messages.
     * @param errors the list of error messages describing the validation failure
     */
    public ValidationException(List<String> errors) {
        super("Validation failed: " + String.join(", ", errors));
        this.result = ValidationResult.failure(errors);
    }

    /**
     * Creates a new ValidationException with the specified list of error messages.
     * @param message the error message describing the validation failure
     */
    public ValidationException(String message) {
        super(message);
        this.result = ValidationResult.failure(message);
    }

    /**
     * Gets the ValidationResult associated with this exception, which contains the details of the validation failure, including any error messages.
     * @return the ValidationResult associated with this exception
     */
    public ValidationResult getResult() {
        return result;
    }

    /**
     * Gets the list of error messages describing the validation failure.
     * @return the list of error messages describing the validation failure
     */
    public List<String> getErrors() {
        return result.getErrors();
    }
}
