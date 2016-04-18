package ru.flashsafe.updater;

import java.nio.file.Path;

import com.google.inject.Inject;

import ru.flashsafe.application.ApplicationService;
import ru.flashsafe.common.partition.Partition;
import ru.flashsafe.partition.PartitionService;

public class UpdaterService {

    private final PartitionService partitionService;
    
    private final ApplicationService applicationService;
    
    @Inject
    UpdaterService(PartitionService partitionService, ApplicationService applicationService) {
        this.partitionService = partitionService;
        this.applicationService = applicationService;
    }

    public boolean update(boolean force) {
        Partition systemPartition = partitionService.lookupFlashsafeSystemPartition();
        Path systemPartitionRoot = systemPartition.getPartitionPath();
        
        return false;
    }

    public boolean update() {
        return update(false);
    }

    public boolean runFileBrowserApplication() {
        Partition userDataPartition = partitionService.lookupDataPartition();
        
        return false;
    }
    
}
