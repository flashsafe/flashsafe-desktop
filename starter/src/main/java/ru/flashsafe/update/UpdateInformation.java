package ru.flashsafe.update;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class UpdateInformation {

    @SerializedName("last_version")
    private String lastVersion;
    
    @SerializedName("last_update")
    private String lastUpdate;
    
    @SerializedName("created_files")
    private List<String> createdFiles;
    
    @SerializedName("created_dirs")
    private List<String> createdDirectories;
    
    @SerializedName("deleted_files")
    private List<String> deletedFiles;
    
    @SerializedName("deleted_dirs")
    private List<String> deletedDirectories;

    public String getLastVersion() {
        return lastVersion;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public List<String> getCreatedFiles() {
        return createdFiles;
    }

    public List<String> getCreatedDirectories() {
        return createdDirectories;
    }

    public List<String> getDeletedFiles() {
        return deletedFiles;
    }

    public List<String> getDeletedDirectories() {
        return deletedDirectories;
    }
}
