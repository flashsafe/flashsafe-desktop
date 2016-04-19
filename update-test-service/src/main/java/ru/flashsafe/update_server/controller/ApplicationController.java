package ru.flashsafe.update_server.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ru.flashsafe.common.application.Application;
import ru.flashsafe.common.fv.checksum.Checksum;
import ru.flashsafe.common.version.Version;
import ru.flashsafe.update_server.service.ApplicationService;

@RestController()
@RequestMapping("/application")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Set<Application> get() {
        return applicationService.findAllApplications();
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Application get(@PathVariable(value = "id") String id) {
        return applicationService.findApplication(id);
    }
    
    @RequestMapping(path = "/{id}/checksum", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Checksum checksum(@PathVariable(value = "id") String id) {
        return applicationService.checksum(id);
    }
    
    @RequestMapping(path = "/{id}/{version}/checksum", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Checksum checksum(@PathVariable(value = "id") String id, @PathVariable(value = "version") String versionString) {
        Version version = Version.fromString(versionString);
        return applicationService.checksum(id, version);
    }

    @RequestMapping(path = "/{id}/package", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<InputStreamResource> download(@PathVariable(value = "id") String id) {
        HttpHeaders respHeaders = new HttpHeaders();
        respHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        // respHeaders.setContentLength(new File(fullPath).length());
        respHeaders.setContentDispositionFormData("attachment", id + "_package.zip");
        try {
            InputStream packageInputStream = applicationService.getApplicationPackageStream(id);
            return new ResponseEntity<InputStreamResource>(new InputStreamResource(packageInputStream), respHeaders,
                    HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<InputStreamResource>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(path = "/{id}/{version}/package", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<InputStreamResource> download(@PathVariable(value = "id") String id,
            @PathVariable(value = "version") String versionString) {
        Version version = Version.fromString(versionString);
        HttpHeaders respHeaders = new HttpHeaders();
        respHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        // respHeaders.setContentLength(new File(fullPath).length());
        respHeaders.setContentDispositionFormData("attachment", id + "_package.zip");
        try {
            InputStream packageInputStream = applicationService.getApplicationPackageStream(id, version);
            return new ResponseEntity<InputStreamResource>(new InputStreamResource(packageInputStream), respHeaders,
                    HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<InputStreamResource>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
