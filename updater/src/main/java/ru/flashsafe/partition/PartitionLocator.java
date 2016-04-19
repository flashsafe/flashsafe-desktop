package ru.flashsafe.partition;

import java.nio.file.Path;

import ru.flashsafe.common.partition.Partition;
import ru.flashsafe.common.partition.Partition.Type;

public interface PartitionLocator {

    Partition lookup(Type partitionType) throws PartitionDetectionException;

    Partition createDataPartition(Path path) throws PartitionCreationException;
    
}
