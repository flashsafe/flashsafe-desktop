package ru.flashsafe.core.old.storage;

import ru.flashsafe.core.file.File;
import ru.flashsafe.core.file.FileObjectType;

import com.fasterxml.jackson.annotation.JsonTypeName;
import java.io.IOException;

@JsonTypeName("FILE")
public class FlashSafeStorageFile extends FlashSafeStorageFileObject implements File {
    
    public FlashSafeStorageFile() {
        setType(FileObjectType.FILE);
    }

    @Override
    public String getFileFormat() throws IOException {
        return getExt();
    }

    @Override
    public String getHash() {
        return getObjectHash();
    }
  
}
