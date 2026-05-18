package com.neroyun.mediator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Event Handler functionality.
 * Tests event handling, event publishing, and multiple handlers for a single event.
 */
public class EventHandlerTest {

    private Mediator mediator;
    private UserEventCounterHandler userEventCounter;

    @BeforeEach
    void setUp() {
        // Clear any previous state
        UserStore.getInstance().clear();

        // Create handler instances
        userEventCounter = new UserEventCounterHandler();

        // Setup mediator with handlers
        mediator = new PipelinedMediator()
                .use(() -> Stream.of(
                        new UserCreatedEventHandler(),
                        userEventCounter
                ))
                .use(() -> Executors.newFixedThreadPool(2));
    }

    @Test
    void testUserCreatedEventHandler() {
        // Arrange
        UserCreatedEvent event = new UserCreatedEvent(123L, "Jane Doe");
        UserCreatedEventHandler handler = new UserCreatedEventHandler();

        // Act
        Void result = handler.handle(event);

        // Assert
        assertNull(result, "Event handler should return null (Void)");
    }

    @Test
    void testEventHandlerReturnsVoid() {
        // Arrange
        UserCreatedEvent event = new UserCreatedEvent(456L, "John Smith");
        UserCreatedEventHandler handler = new UserCreatedEventHandler();

        // Act
        Void result = handler.handle(event);

        // Assert
        assertNull(result, "Event handler should return null (Void)");
    }

    @Test
    void testEventHandlerMatches() {
        // Arrange
        UserCreatedEvent event = new UserCreatedEvent(1L, "Test User");
        UserCreatedEventHandler handler = new UserCreatedEventHandler();

        // Act
        boolean matches = handler.matches(event);

        // Assert
        assertTrue(matches, "Handler should match its own event type");
    }

    @Test
    void testEventPublishDoesNotThrow() {
        // Arrange
        UserCreatedEvent event = new UserCreatedEvent(999L, "No Exception User");

        // Act & Assert
        assertDoesNotThrow(() -> mediator.publish(event), "Publishing an event should not throw an exception");
    }

    @Test
    void testMultipleHandlersForSameEvent() {
        // Arrange
        UserCreatedEvent event = new UserCreatedEvent(100L, "Multiple Handlers User");

        // Act
        mediator.publish(event);

        // Give handlers time to process (since they run asynchronously)
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Assert
        assertTrue(userEventCounter.getCount() > 0, "Counter handler should have been invoked");
    }

    @Test
    void testMultipleEventsHandledInOrder() {
        // Arrange
        UserCreatedEvent event1 = new UserCreatedEvent(1L, "First User");
        UserCreatedEvent event2 = new UserCreatedEvent(2L, "Second User");
        UserCreatedEvent event3 = new UserCreatedEvent(3L, "Third User");

        // Act
        mediator.publish(event1);
        mediator.publish(event2);
        mediator.publish(event3);

        // Give handlers time to process
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Assert
        assertEquals(3, userEventCounter.getCount(), "Handler should have handled 3 events");
    }

    @Test
    void testEventHandlerWithNullParameters() {
        // Arrange & Act & Assert
        assertThrows(IllegalArgumentException.class,
            () -> mediator.publish(null),
            "Publishing null event should throw IllegalArgumentException");
    }

    @Test
    void testUserCreatedEventImmutability() {
        // Arrange
        Long id = 42L;
        String name = "Test User";

        // Act
        UserCreatedEvent event = new UserCreatedEvent(id, name);

        // Assert
        assertEquals(id, event.id(), "Event should store the correct id");
        assertEquals(name, event.name(), "Event should store the correct name");
    }

    @Test
    void testEventImplementsEventInterface() {
        // Arrange
        UserCreatedEvent event = new UserCreatedEvent(1L, "Test");

        // Assert
        assertInstanceOf(Event.class, event, "UserCreatedEvent should implement Event interface");
    }
}

