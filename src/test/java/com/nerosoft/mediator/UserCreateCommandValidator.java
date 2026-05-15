package com.nerosoft.mediator;

import com.nerosoft.mediator.validation.ValidationResult;

public class UserCreateCommandValidator implements Validator<UserCreateCommand> {
    @Override
    public ValidationResult validate(UserCreateCommand message) {
        if (message.name() == null || message.name().isBlank()) {
            return ValidationResult.failure("Name is required");
        }

        if (message.email() == null || message.email().isBlank()) {
            return ValidationResult.failure("Email is required");
        }

        if (!message.email().contains("@")) {
            return ValidationResult.failure("Email address is invalid");
        }

        return ValidationResult.success();
    }
}
