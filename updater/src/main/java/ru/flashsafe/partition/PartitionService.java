package ru.flashsafe.partition;

import ru.flashsafe.common.partition.Partition;
import ru.flashsafe.common.partition.Partition.Type;

import com.google.inject.Inject;

public final class PartitionService {

    private final PartitionLocator partitionLocator;
    
    @Inject
    private PartitionService(PartitionLocator partitionLocator) {
        this.partitionLocator = partitionLocator;
    }

    public Partition lookupFlashsafeSystemPartition() throws PartitionDetectionException {
        return partitionLocator.lookup(Type.FLASHSAFE_SYSTEM);
    }

    public Partition lookupDataPartition() throws PartitionDetectionException {
        return partitionLocator.lookup(Type.USER_DATA);
    }
    
    public boolean configureDataPartition() {
        
        return true;
    }
}
