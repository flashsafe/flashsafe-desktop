package ru.flashsafe.partition;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.nio.file.Paths;

import ru.flashsafe.common.partition.Partition;

public class FlashsafeDataPartition implements Partition {

    private final String rootPath;
    
    public FlashsafeDataPartition(String rootPath) {
        this.rootPath = requireNonNull(rootPath, "Root path can not be null");
    }

    @Override
    public Path getPartitionPath() {
        return Paths.get(rootPath);
    }

    @Override
    public Type getType() {
        return Type.USER_DATA;
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
        FlashsafeDataPartition other = (FlashsafeDataPartition) obj;
        if (rootPath == null) {
            if (other.rootPath != null)
                return false;
        } else if (!rootPath.equals(other.rootPath))
            return false;
        return true;
    }

}
