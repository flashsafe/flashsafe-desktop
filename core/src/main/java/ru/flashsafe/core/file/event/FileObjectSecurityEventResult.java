package ru.flashsafe.core.file.event;

public class FileObjectSecurityEventResult {

    private final ResultType result;
    
    private final String code;

    public FileObjectSecurityEventResult(ResultType result, String code) {
        this.result = result;
        this.code = code;
    }

    /**
     * @return pincode value
     */
    public String getCode() {
        return code;
    }

    /**
     * @return
     */
    public ResultType getResult() {
        return result;
    }

    public enum ResultType {
        
        CONTINUE,

        CANCEL

    }

}
