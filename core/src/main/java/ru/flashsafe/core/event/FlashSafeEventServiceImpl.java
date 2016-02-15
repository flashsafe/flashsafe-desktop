package ru.flashsafe.core.event;

import com.google.common.eventbus.EventBus;
import com.google.inject.Singleton;

@Singleton
public class FlashSafeEventServiceImpl implements FlashSafeEventService {

    private EventBus eventBus = new EventBus("FlashSafeEventBus");
    
    FlashSafeEventServiceImpl() {
    }
    
    @Override
    public void registerSubscriber(Object subscriber) {
        eventBus.register(subscriber);
    }

    @Override
    public void unRegisterSubscriber(Object subscriber) {
        eventBus.unregister(subscriber);
    }
    
    public void postEvent(Object event) {
        eventBus.post(event);
    }
    
}
