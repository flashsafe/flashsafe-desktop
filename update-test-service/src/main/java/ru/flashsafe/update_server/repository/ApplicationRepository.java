package ru.flashsafe.update_server.repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import ru.flashsafe.common.application.Application;
import ru.flashsafe.common.fv.checksum.Checksum;
import ru.flashsafe.common.version.Version;

public interface ApplicationRepository {

    Application findById(String id);
    
    Set<Application> findAllApplications();
    
    InputStream getApplicationPackageStream(String id) throws IOException;
    
    InputStream getApplicationPackageStream(String id, Version version) throws IOException;
    
    Checksum checksum(String id);
    
    Checksum checksum(String id, Version version);
    
}
