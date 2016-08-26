package ru.flashsafe.core.old.storage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.flashsafe.core.old.storage.rest.CustomMultipart;
import ru.flashsafe.core.old.storage.rest.FlashSafeAuthClientFilter;
import ru.flashsafe.core.old.storage.rest.data.UploadResponse;
import ru.flashsafe.core.operation.OperationResult;
import ru.flashsafe.core.operation.OperationState;
import ru.flashsafe.core.storage.SingleFileStorageOperation;
import ru.flashsafe.core.storage.exception.FlashSafeStorageException;

/**
 *
 * @author Alexander Krysin
 */
public class UploadFileFuture implements RunnableFuture {
    private String dirHash;
    private String fileHash;
    private Path file;
    private long partsCount;
    private DefaultFlashSafeStorageService storageService;
    private SingleFileStorageOperation operation;
    private List<Future> partUploads = new ArrayList<>();
    
    private boolean isCancelled = false;
    private boolean isDone = false;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadFileFuture.class);
    
    public UploadFileFuture(String dirHash, String fileHash, Path file, DefaultFlashSafeStorageService storageService, SingleFileStorageOperation operation) {
        this.dirHash = dirHash;
        this.fileHash = fileHash;
        this.file = file;
        this.storageService = storageService;
        this.partsCount = file.toFile().length() / (512 * 1024);
        if(file.toFile().length() % (512 * 1024) > 0) this.partsCount++;
        this.operation = operation;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        for(Future f : partUploads) f.cancel(true);
        isCancelled = true;
        return isCancelled;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public boolean isDone() {
        isDone = true;
        for(Future f : partUploads) {
            if(!f.isDone()) isDone = false;
        }
        return isDone;
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        return null;
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }
    
    private void incrementProgress() {
        operation.setProcessedBytes(operation.getProcessedBytes() + (512 * 1024));
    }

    private synchronized void uploadPart(int j) throws FlashSafeStorageException {
        FormDataMultiPart multiPart = new FormDataMultiPart();
                multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
                multiPart.field("access_token", FlashSafeAuthClientFilter.getAuthData().getToken());
                multiPart.field("dir_hash", dirHash);
        if(fileHash != null) multiPart.field("file_hash", fileHash);
        StreamDataBodyPart bp = (StreamDataBodyPart) getPartStream(j)[1];
        multiPart.bodyPart(bp);
        long length = 139 + (fileHash != null ? Integer.MAX_VALUE : 0) + calculateContentLength(dirHash, file, (int) getPartStream(j)[0]);
        partUploads.add(storageService.directoryTarget
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header(HttpHeaders.CONTENT_LENGTH, length)
                .async()
                .post(Entity.entity(multiPart, multiPart.getMediaType()), new InvocationCallback<UploadResponse>() {

            @Override
            public void completed(UploadResponse response) {
                if(fileHash == null) fileHash = response.getResponseMeta().getFileHash();
                incrementProgress();
                //operation.setProgress(j / (int) partsCount * 100);
                if(j == partsCount) setStatusToFinished(operation, OperationResult.SUCCESS);
            }

            @Override
            public void failed(Throwable throwable) {
                if (throwable instanceof CancellationException) {
                    setStatusToFinished(operation, OperationResult.CANCELED);
                    LOGGER.debug("Uploading of {} to {} was canceled", file, dirHash);
                } else {
                    setStatusToFinished(operation, OperationResult.ERROR);
                    LOGGER.warn("Error while uploading file " + file + " to directory with id " + dirHash, throwable);
                }
            }

        }));
    }
    
    @Override
    public void run() {
            for(int i=1;i<=partsCount;i++) {
                try {
                    uploadPart(i);
                } catch (FlashSafeStorageException e) {
                    LOGGER.warn("Error while uploading file " + file + " to directory with id " + dirHash, e);
                }
            }
    }
    
    private Object[] getPartStream(long partNum) {
        Object[] result = new Object[2];
        StreamDataBodyPart bodyPart = null;
        int size = 0;
        try {
            InputStream fis = Files.newInputStream(file);
            byte[] part = new byte[512 * 1024];
            fis.skip(512 * 1024 * (partNum - 1));
            size = fis.read(part);
            InputStream in = new ByteArrayInputStream(part);
            bodyPart = new StreamDataBodyPart("file", in, file.toFile().getName());
            result = new Object[] {size, bodyPart};
        } catch(IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    private static void setStatusToFinished(SingleFileStorageOperation operation, OperationResult result) {
        operation.setResult(result);
        operation.setState(OperationState.FINISHED);
        operation.markAsFinished();
    }
    
    /**
     * Calculates content-length using a lot of calculated guesses..
     * 
     * Attention! CustomMultipart.class has to be registered in Jersey's config!
     * ...or this method will be spoiled #####
     * 
     * 1. We send FormDataMultiPart which consists of 2 parts: - directory Id
     * field, - file stream body part. 213 is a number of bytes produces by
     * Jersey for these 2 parts. The we add directoryId length; Then we add file
     * name - it can be different... Then the file length.
     * 
     * @param directoryId
     * @param fileToLoad
     * @return
     * @throws FlashSafeStorageException
     * @throws UnsupportedEncodingException
     */
    public static long calculateContentLength(String directoryHash, Path fileToLoad, int size) throws FlashSafeStorageException {
        Objects.requireNonNull(fileToLoad);
        long length = 213;
        String boundaryString = CustomMultipart.generateBoundary();
        length += boundaryString.length() * 2;
        length += directoryHash.length();
        length += fileToLoad.toFile().getName().getBytes(StandardCharsets.UTF_8).length;
        length += size;
        return length;
    }
    
}
