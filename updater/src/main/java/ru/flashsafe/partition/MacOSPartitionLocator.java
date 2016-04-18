package ru.flashsafe.partition;

import java.nio.file.Path;

import ru.flashsafe.common.partition.Partition;


public class MacOSPartitionLocator extends BasePartitionLocator implements PartitionLocator {

    @Override
    protected Partition lookupDataPartition() {
        // replace this code to add OS-dependent hardware detection
        return super.lookupDataPartition();
    }

    @Override
    protected boolean verifyPartition(String identifier, Path dataPartitionRoot) {
        // TODO add verification process (we use data from flashsafe web service
        // only as there is no access to token on OS X)
        return true;
    }

}
