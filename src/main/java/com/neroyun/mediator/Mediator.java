package com.neroyun.mediator;

import com.neroyun.mediator.internal.QueryCallback;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Defines the Mediator interface for handling commands, queries, and events asynchronously.
 * The Mediator pattern promotes loose coupling between components by centralizing communication.
 * This interface can be implemented to create a concrete mediator that manages the interactions between various components in the system.
 * All operations are asynchronous and return CompletableFuture for better performance and scalability.
 */
@SuppressWarnings("unused")
public interface Mediator {

    /**
     * Sends a command to the appropriate handler asynchronously.
     *
     * @param command the command to be sent
     * @param <T>     the type of the command
     * @return a CompletableFuture that completes when the command is processed
     */
    <T extends Command> CompletableFuture<Void> sendAsync(T command);

    /**
     * Sends a command to the appropriate handler asynchronously with a contextConsumer consumer for additional metadata.
     *
     * @param command         the command to be sent
     * @param contextConsumer the contextConsumer consumer for additional metadata
     * @param <T>             the type of the command
     * @return a CompletableFuture that completes when the command is processed
     */
    <T extends Command> CompletableFuture<Void> sendAsync(T command, Consumer<MessageContext> contextConsumer);

    /**
     * Executes a query asynchronously and returns the result.
     *
     * @param query the query to be executed
     * @param <T>   the type of the query
     * @param <R>   the type of the result
     * @return a CompletableFuture containing the result of the query
     */
    <T extends Query<R>, R> CompletableFuture<R> executeAsync(T query);

    /**
     * Executes a query asynchronously with a contextConsumer consumer for additional metadata and returns the result.
     *
     * @param query           the query to be executed
     * @param contextConsumer the contextConsumer consumer for additional metadata
     * @param <T>             the type of the query
     * @param <R>             the type of the result
     * @return a CompletableFuture containing the result of the query
     */
    <T extends Query<R>, R> CompletableFuture<R> executeAsync(T query, Consumer<MessageContext> contextConsumer);

    /**
     * Executes a query asynchronously and provides the result to the specified response handler.
     *
     * @param query    the query to be executed
     * @param callback the callback to handle the result of the query
     * @param <T>      the type of the query
     * @param <R>      the type of the result
     * @return a CompletableFuture that completes when the callback is invoked
     */
    <T extends Query<R>, R> CompletableFuture<Void> executeAsync(T query, QueryCallback<R> callback);

    /**
     * Publishes an event to all interested handlers asynchronously.
     *
     * @param event the event to be published
     * @param <T>   the type of the event
     * @return a CompletableFuture that completes when all event handlers have processed the event
     */
    <T extends Event> CompletableFuture<Void> publishAsync(T event);
}
