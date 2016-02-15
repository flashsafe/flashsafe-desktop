package ru.flashsafe.core.file.event;

/**
 * Result of {@link FileObjectDuplicationHandler}'s work.
 * 
 * @author Andrew
 *
 */
public class FileObjectDuplicationEventResult {
    
    private final ResultType result;
    
    private final boolean applyToAll;
    
    public FileObjectDuplicationEventResult(ResultType result, boolean applyToAll) {
        this.result = result;
        this.applyToAll = applyToAll;
    }
    
    public FileObjectDuplicationEventResult(ResultType result) {
        this(result, false);
    }
    
    /**
     * @return
     */
    public ResultType getResult() {
        return result;
    }

    /**
     * @return 
     */
    public boolean isApplyToAll() {
        return applyToAll;
    }

    public enum ResultType {
        
        COPY_AND_REWRITE,
        
        DO_NOT_COPY,
        
        COPY_WITH_NEW_NAME,
        
        SKIP,
        
        CANCEL
        
    }
    
}
