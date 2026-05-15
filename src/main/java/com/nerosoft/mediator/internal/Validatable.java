package com.nerosoft.mediator.internal;

/**
 * Represents a marker interface for objects that can be validated.
 * This interface is used to indicate that a class has validation logic associated with it, and it can be used in conjunction with the Validator interface to perform validation on instances of classes that implement Validatable.
 * By implementing this interface, a class can be recognized as being subject to validation rules, allowing for a consistent approach to validating objects within the mediator pattern.
 */
public interface Validatable {
}
