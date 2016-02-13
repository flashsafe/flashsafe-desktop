package ru.flashsafe.core.operation;

import java.util.concurrent.atomic.AtomicLong;

public class OperationIDGenerator {

    private static AtomicLong counter = new AtomicLong(0);

    private OperationIDGenerator() {
    }

    public static long nextId() {
        return counter.incrementAndGet();
    }

}
