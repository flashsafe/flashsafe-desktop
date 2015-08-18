package ru.flashsafe.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.flashsafe.core.event.ApplicationStopEvent;
import ru.flashsafe.core.event.FlashSafeEventService;
import ru.flashsafe.core.impl.DefaultFlashSafeSystem;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Central object to provide control for an application and access to its objects.
 * 
 * @author Andrew
 *
 */
public class FlashSafeApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlashSafeApplication.class);

    private static final Injector injector;
    
    private static boolean applicationStarted = false;

    private static FlashSafeSystem flashSafeSystem;

    static {
        injector = Guice.createInjector(new CoreModule());
    }
    
    private FlashSafeApplication() {
    }

    /**
     * Retrieves a {@link FlashSafeSystem} instance from FlashSafeApplication context.
     * This method can be used only if the application already runs.
     * 
     * @return the FlashSafeSystem instance
     * @throws IllegalStateException if the application is not running
     */
    public static FlashSafeSystem flashSafeSystem() throws IllegalStateException {
        if (!applicationStarted) {
            throw new IllegalStateException("Can not get FlashSafeSystem instance. The application is not running");
        }
        return flashSafeSystem;
    }

    /**
     * Runs the application.
     * 
     * @throws IllegalStateException if the application is already started
     */
    public static synchronized void run() throws IllegalStateException {
        if (applicationStarted) {
            throw new IllegalStateException("The application is already started");
        }
        LOGGER.info("Lookup FlashSafeSystem");
        flashSafeSystem = lookupFlashSafeSystem();
        applicationStarted = true;
    }
    
    /**
     * Stops the application.
     * 
     * @throws IllegalStateException if the application is already stopped
     */
    public static synchronized void stop() throws IllegalStateException {
        if (!applicationStarted) {
            throw new IllegalStateException("The application is already stopped");
        }
        stopApplication();
        applicationStarted = false;
    }

    private static FlashSafeSystem lookupFlashSafeSystem() {
        return injector.getInstance(DefaultFlashSafeSystem.class);
    }
    
    private static void stopApplication() {
        FlashSafeEventService eventService = injector.getInstance(FlashSafeEventService.class);
        eventService.postEvent(new ApplicationStopEvent());
    }
    
}
