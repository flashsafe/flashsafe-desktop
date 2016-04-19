package ru.flashsafe.partition;

import java.nio.file.Path;

import ru.flashsafe.common.partition.Partition;
import ru.flashsafe.common.partition.Partition.Type;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
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
    
    public Partition configureDataPartition(Path rootPath) {
        partitionLocator.createDataPartition(rootPath);
        return null;
    }
}
