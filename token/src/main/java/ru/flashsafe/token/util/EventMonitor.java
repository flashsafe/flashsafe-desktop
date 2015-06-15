package ru.flashsafe.token.util;

/**
 * 
 * 
 * @author Andrew
 *
 */
public class EventMonitor implements Runnable {

    private static final long DEFAULT_DELAY = 1000;
    
    private Thread monitorThread;
    
    private volatile boolean shouldContinue = true;
    
    private final EventHandler handler;
    
    private final String name;
    
    private final long delay;
    
    public EventMonitor(String name, EventHandler handler, long delay) {
        this.name = name;
        this.handler = handler;
        this.delay = delay;
    }
    
    public EventMonitor(String name, EventHandler handler) {
        this(name, handler, DEFAULT_DELAY);
    }

    public void start() {
        if (monitorThread == null) {
            monitorThread = new Thread(this);
            monitorThread.setName(name);
            monitorThread.setPriority(Thread.MIN_PRIORITY);
            monitorThread.setDaemon(true);
        }
        monitorThread.start();
    }
    
    public void stop() {
        shouldContinue = false;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted() && shouldContinue) {
            handler.onEvent();
            if (delay > 0) { 
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    continue;
                }
            }
        }
    }
    
    public static interface EventHandler {
        
        void onEvent();
        
    }
}
