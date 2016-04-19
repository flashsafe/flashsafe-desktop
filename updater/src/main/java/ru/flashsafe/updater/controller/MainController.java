package ru.flashsafe.updater.controller;

import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.flashsafe.application.ApplicationService;
import ru.flashsafe.application.MandatoryApplications;
import ru.flashsafe.common.application.Application;
import ru.flashsafe.common.application.InstalledApplication;
import ru.flashsafe.common.version.Version;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class MainController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    private final ApplicationService applicationService;

    @Inject
    MainController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    public void runFileBrowser() {
        InstalledApplication fileBrowserApp = null;
        try {
            fileBrowserApp = applicationService.getInstalledApplication(MandatoryApplications.FILE_BROWSER_NAME);
            boolean needToUpdate = checkIfNeedToUpdate(fileBrowserApp);
            if (needToUpdate) {
                applicationService.update(fileBrowserApp);
            }
        } catch (IllegalStateException e) {
            LOGGER.info("Installing application {}", MandatoryApplications.FILE_BROWSER_NAME);
            applicationService.install(MandatoryApplications.FILE_BROWSER_APPLICATION);
            fileBrowserApp = applicationService.getInstalledApplication(MandatoryApplications.FILE_BROWSER_NAME);
        }
        try {
            Process appProcess = applicationService.run(fileBrowserApp);
            //TODO get result from the application process
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.warn("Error while running " + MandatoryApplications.FILE_BROWSER_NAME, e);
        }
    }
    
    private boolean checkIfNeedToUpdate(InstalledApplication application) {
        Version currentVersion = application.getCurrentVersion();
        Version latestVersion = application.getLatestVersion();
        if (latestVersion == Version.UNDEFINED) {
            Application availableToInstall = applicationService.getApplication(application.getId());
            latestVersion = availableToInstall.getLatestVersion();
        }
        return !currentVersion.equals(latestVersion);
    }

}
