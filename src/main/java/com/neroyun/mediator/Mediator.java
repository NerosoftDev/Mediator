package com.neroyun.mediator;

import com.neroyun.mediator.internal.QueryCallback;

/**
 * Defines the Mediator interface for handling commands, queries, and events.
 * The Mediator pattern promotes loose coupling between components by centralizing communication.
 * This interface can be implemented to create a concrete mediator that manages the interactions between various components in the system.
 */
@SuppressWarnings("unused")
public interface Mediator {

    /**
     * Sends a command to the appropriate handler.
     * @param command the command to be sent
     * @param <T> the type of the command
     */
    <T extends Command> void send(T command);

    /**
     * Executes a query and returns the result.
     * @param query the query to be executed
     * @param <T> the type of the query
     * @param <R> the type of the result
     * @return the result of the query
     */
    <T extends Query<R>, R> R execute(T query);

    /**
     * Executes a query and provides the result to the specified response handler.
     * @param query the query to be executed
     * @param callback the callback to handle the result of the query
     * @param <T> the type of the query
     * @param <R> the type of the result
     */
    <T extends Query<R>, R> void execute(T query, QueryCallback<R> callback);

    /**
     * Publishes an event to all interested handlers.
     * @param event the event to be published
     * @param <T> the type of the event
     */
    <T extends Event> void publish(T event);
}
