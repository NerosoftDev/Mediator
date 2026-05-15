package com.nerosoft.mediator.validation;

import java.util.List;

/**
 * Defines the result of a validation operation, which can be either successful or failed with a list of error messages.
 * This class provides static factory methods to create success or failure results, and it allows checking the status of the validation and retrieving any error messages if the validation failed.
 * The ValidationResult class is designed to be immutable and thread-safe, making it suitable for use in concurrent environments where multiple threads may be performing validation operations simultaneously.
 * By encapsulating the validation result in a dedicated class, it promotes a clear and consistent way to handle validation outcomes throughout the application, allowing for better error handling and improved code readability when dealing with validation logic in the mediator pattern.
 */
public final class ValidationResult {
    private static final ValidationResult SUCCESS = new ValidationResult(List.of());

    private final List<String> errors;

    public ValidationResult(List<String> errors) {
        this.errors = errors;
    }

    public static ValidationResult success() {
        return SUCCESS;
    }

    public static ValidationResult failure(List<String> errors) {
        return new ValidationResult(errors);
    }

    public static ValidationResult failure(String message) {
        return new ValidationResult(List.of(message));
    }

    public List<String> getErrors() {
        return errors;
    }

    public boolean isSuccess() {
        return errors.isEmpty();
    }

    public boolean isFailure() {
        return !isSuccess();
    }

    @Override
    public String toString() {
        return isSuccess()? "ValidationResult{success}" : "ValidationResult{failure, errors=" + errors + "}";
    }
}
