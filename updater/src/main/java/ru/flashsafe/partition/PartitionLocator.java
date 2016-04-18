package ru.flashsafe.partition;

import ru.flashsafe.common.partition.Partition;
import ru.flashsafe.common.partition.Partition.Type;

public interface PartitionLocator {

    Partition lookup(Type partitionType) throws PartitionDetectionException;

}
