package ru.flashsafe.update_server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartupHandler implements ApplicationListener<ApplicationReadyEvent> {
    
    @Autowired
    private ApplicationLocator applicationLocator;
    
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        System.out.println("App was started!");
        applicationLocator.findAllApplication();
    }

}
