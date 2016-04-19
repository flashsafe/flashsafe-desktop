package ru.flashsafe.update_server.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.flashsafe.common.application.Application;
import ru.flashsafe.common.fv.checksum.Checksum;
import ru.flashsafe.common.version.Version;
import ru.flashsafe.update_server.repository.ApplicationRepository;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    public ApplicationService() {
    }

    public Set<Application> findAllApplications() {
        return applicationRepository.findAllApplications();
    }

    public Application findApplication(String applicationId) {
        return applicationRepository.findById(applicationId);
    }

    public InputStream getApplicationPackageStream(String applicationId) throws IOException {
        return applicationRepository.getApplicationPackageStream(applicationId);
    }

    public InputStream getApplicationPackageStream(String applicationId, Version version) throws IOException {
        return applicationRepository.getApplicationPackageStream(applicationId, version);
    }
    
    public Checksum checksum(String applicationId) {
        return applicationRepository.checksum(applicationId);
    }
    
    public Checksum checksum(String applicationId, Version version) {
        return applicationRepository.checksum(applicationId, version);
    }

}
