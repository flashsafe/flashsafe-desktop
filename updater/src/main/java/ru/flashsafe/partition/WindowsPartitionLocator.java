package ru.flashsafe.partition;

import java.nio.file.Path;

import ru.flashsafe.common.partition.Partition;


public class WindowsPartitionLocator extends BasePartitionLocator implements PartitionLocator {

    @Override
    protected Partition lookupDataPartition() {
        // replace this code to add OS-dependent hardware detection
        return super.lookupDataPartition();
    }

    @Override
    protected boolean verifyPartition(String identifier, Path dataPartitionRoot) {
        //TODO add verification process (use data from token, flashsafe web service)
        return true;
    }

}
