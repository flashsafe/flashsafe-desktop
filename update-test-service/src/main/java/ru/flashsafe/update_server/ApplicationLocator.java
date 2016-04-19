package ru.flashsafe.update_server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import ru.flashsafe.common.application.Application;
import ru.flashsafe.common.fv.checksum.Checksum;
import ru.flashsafe.common.version.Version;

public interface ApplicationLocator {

    Application findApplication(String id);
    
    Set<Application> findAllApplication();
    
    InputStream getApplicationPackageStream(String id) throws IOException;
    
    InputStream getApplicationPackageStream(String id, Version version) throws IOException;
    
    Checksum checksum(String id);
    
    Checksum checksum(String id, Version version);
}
