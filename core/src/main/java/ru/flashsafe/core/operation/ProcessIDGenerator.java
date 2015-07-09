package ru.flashsafe.core.operation;

import java.util.concurrent.atomic.AtomicLong;

public class ProcessIDGenerator {

    private static AtomicLong counter = new AtomicLong(0);

    private ProcessIDGenerator() {
    }

    public static long nextId() {
        return counter.incrementAndGet();
    }

}
