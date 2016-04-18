package ru.flashsafe.partition;

import java.nio.file.Path;
import java.nio.file.Paths;

import ru.flashsafe.common.partition.Partition;
import static java.util.Objects.*;

public class FlashsafeSystemPartition implements Partition {

    private final String rootPath;
    
    public FlashsafeSystemPartition(String rootPath) {
        this.rootPath = requireNonNull(rootPath, "Root path can not be null");
    }

    @Override
    public Path getPartitionPath() {
        return Paths.get(rootPath);
    }
    
    @Override
    public Type getType() {
        return Type.FLASHSAFE_SYSTEM;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((rootPath == null) ? 0 : rootPath.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FlashsafeSystemPartition other = (FlashsafeSystemPartition) obj;
        if (rootPath == null) {
            if (other.rootPath != null)
                return false;
        } else if (!rootPath.equals(other.rootPath))
            return false;
        return true;
    }
}
