package ru.flashsafe.common.partition;

import java.nio.file.Path;

public interface Partition {

    enum Type {
        
        /**
         * READ-ONLY partition with the system files.
         */
        FLASHSAFE_SYSTEM,
        
        /**
         * 
         */
        USER_DATA
        
    }
    
    Path getPartitionPath();
    
    Type getType();
    
}
