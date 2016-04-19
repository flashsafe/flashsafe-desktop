package ru.flashsafe.update_server.repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ru.flashsafe.common.application.Application;
import ru.flashsafe.common.fv.checksum.Checksum;
import ru.flashsafe.common.version.Version;
import ru.flashsafe.update_server.ApplicationLocator;

@Repository
public class ApplicationRepositoryImpl implements ApplicationRepository {

    @Autowired
    private ApplicationLocator applicationLocator;
    
    @Override
    public Application findById(String id) {
        return applicationLocator.findApplication(id);
    }

    @Override
    public Set<Application> findAllApplications() {
        return applicationLocator.findAllApplication();
    }
    
    @Override
    public InputStream getApplicationPackageStream(String id) throws IOException {
        return applicationLocator.getApplicationPackageStream(id);
    }
    
    @Override
    public InputStream getApplicationPackageStream(String id, Version version) throws IOException {
        return applicationLocator.getApplicationPackageStream(id, version);
    }
    
    
    @Override
    public Checksum checksum(String id) {
        return applicationLocator.checksum(id);
    }
    
    @Override
    public Checksum checksum(String id, Version version) {
        return applicationLocator.checksum(id, version);
    }

}
