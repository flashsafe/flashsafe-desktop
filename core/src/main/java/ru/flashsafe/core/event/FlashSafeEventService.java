package ru.flashsafe.core.event;

/**
 * 
 * 
 * @author Andrew
 *
 */
public interface FlashSafeEventService {

    void registerSubscriber(Object subscriber);
    
    void unRegisterSubscriber(Object subscriber);
 
    /**
     * Posts event.
     * 
     * @param event
     */
    void postEvent(Object event);
    
}
