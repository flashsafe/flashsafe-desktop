package ru.flashsafe.application;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.List;

import ru.flashsafe.loader.LoaderService;
import ru.flashsafe.common.application.Application;
import ru.flashsafe.common.application.InstalledApplication;
import ru.flashsafe.partition.PartitionService;

import com.google.inject.Inject;

public class ApplicationService {
    
    private final LoaderService loaderService;
    
    private final PartitionService partitionService;
    
    @Inject
    ApplicationService(PartitionService partitionService, LoaderService loaderService) {
        this.partitionService = partitionService;
    }
    
    public List<InstalledApplication> getAllInstalledApplications() {
        return Collections.emptyList();
    }
    
    public InstalledApplication getInstalledApplication(String applicationName) {
        return null;
    }
    
    public boolean install(Application application) {
        requireNonNull(application);
        return false;
    }

    public boolean update(InstalledApplication application) {
        requireNonNull(application);
        return false;
    }
    
    public boolean repair(InstalledApplication application) {
        requireNonNull(application);
        return false;
    }
    
    public boolean uninstall(InstalledApplication application) {
        requireNonNull(application);
        return false;
    }

    public boolean run(InstalledApplication application) {
        requireNonNull(application);
        return false;
    }
    
}
