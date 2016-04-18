package ru.flashsafe.updater;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Updater {

    private final Injector injector;
    
    public Updater() {
        injector = Guice.createInjector(new UpdaterModule());
    }
    
    public boolean updateFileBrowserApplication() {
        UpdaterService updaterService = injector.getInstance(UpdaterService.class);
        return updaterService.update();
    }

    public boolean runFileBrowserApplication() {
        UpdaterService updaterService = injector.getInstance(UpdaterService.class);
        return updaterService.runFileBrowserApplication();
    }
    
}
