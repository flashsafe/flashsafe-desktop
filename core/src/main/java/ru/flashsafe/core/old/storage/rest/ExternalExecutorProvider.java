package ru.flashsafe.core.old.storage.rest;

import java.util.concurrent.ExecutorService;

import javax.inject.Named;

import org.glassfish.jersey.client.ClientAsyncExecutor;
import org.glassfish.jersey.spi.ExecutorServiceProvider;

@Named("ExternalExecutorProvider")
@ClientAsyncExecutor
public class ExternalExecutorProvider implements ExecutorServiceProvider {
    
    private final ExecutorService executorService;
    
    public ExternalExecutorProvider(ExecutorService executorService) {
        this.executorService = executorService;
    }
    
    @Override
    public ExecutorService getExecutorService() {
        return executorService;
    }

    @Override
    public void dispose(ExecutorService executorService) {
        // Nothing to do.
    }
}
