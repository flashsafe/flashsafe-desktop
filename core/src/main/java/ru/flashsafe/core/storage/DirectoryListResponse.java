package ru.flashsafe.core.storage;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DirectoryListResponse {

    @XmlElement(name = "meta")
    private ResponseMeta responseMeta;
    
    @XmlElement(name = "data")
    private List<FlashSafeStorageFileObject> fileObjects;

    public ResponseMeta getResponseMeta() {
        return responseMeta;
    }

    public void setResponseMeta(ResponseMeta responseMeta) {
        this.responseMeta = responseMeta;
    }

    public List<FlashSafeStorageFileObject> getFileObjects() {
        return fileObjects;
    }

    public void setFileObjects(List<FlashSafeStorageFileObject> fileObjects) {
        this.fileObjects = fileObjects;
    }
    
}
