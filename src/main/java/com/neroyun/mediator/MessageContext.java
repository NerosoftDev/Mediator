package com.neroyun.mediator;

import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

public final class MessageContext {
    private final Flow.Publisher<Object> publisher = new SubmissionPublisher<>();
    private MessageMetadata metadata = new MessageMetadata();
    private String messageId;
    private String requestTraceId;
    private String conversationId;
    private String authorization;

    public MessageContext(String messageId) {
        this.messageId = messageId;
//        Flow.Subscriber<Object> subscriber = new Flow.Subscriber<Object>() {
//            @Override
//            public void onSubscribe(Flow.Subscription subscription) {
//                subscription.request(Long.MAX_VALUE);
//            }
//
//            @Override
//            public void onNext(Object item) {
//                // Handle the received item
//            }
//
//            @Override
//            public void onError(Throwable throwable) {
//                // Handle the error
//            }
//
//            @Override
//            public void onComplete() {
//                // Handle the completion
//            }
//        };
//        publisher.subscribe(subscriber);
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getRequestTraceId() {
        return requestTraceId;
    }

    public void setRequestTraceId(String requestTraceId) {
        this.requestTraceId = requestTraceId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public MessageMetadata getMetadata() {
        return metadata;
    }

    public void subscribe(Flow.Subscriber<Object> subscriber) {
        publisher.subscribe(subscriber);
    }

    public void onComplete(Object item) {
        ((SubmissionPublisher<Object>) publisher).submit(item);
    }

    public void onError(Throwable t) {
        ((SubmissionPublisher<Object>) publisher).closeExceptionally(t);
    }
}
