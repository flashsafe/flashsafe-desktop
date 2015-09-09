package ru.flashsafe.util;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WaitForEvent {

    private static final Logger LOGGER = LoggerFactory.getLogger(WaitForEvent.class);
    
    private final Lock lock = new ReentrantLock();
    
    private final Condition eventHappened = lock.newCondition();
    
    public WaitForEvent() {
    }
    
    public void happened() {
        lock.lock();
        try {
            eventHappened.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void waitEvent() {
        lock.lock();
        try {
            eventHappened.await();
        } catch (InterruptedException e) {
            LOGGER.warn("Interrupted", e);
        } finally {
            lock.unlock();
        }
    }
    
}
