package ru.flashsafe.core.old.storage;

import static java.net.HttpURLConnection.HTTP_OK;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;
import org.glassfish.jersey.message.MessageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.flashsafe.core.FlashSafeRegistry;
import ru.flashsafe.core.event.ApplicationStopEvent;
import ru.flashsafe.core.event.FlashSafeEventService;
import ru.flashsafe.core.file.impl.FileOperationInfo;
import ru.flashsafe.core.old.storage.rest.ContentTypeFixerFilter;
import ru.flashsafe.core.old.storage.rest.ExternalExecutorProvider;
import ru.flashsafe.core.old.storage.rest.FlashSafeAuthClientFilter;
import ru.flashsafe.core.old.storage.rest.data.CopyResponse;
import ru.flashsafe.core.old.storage.rest.data.CopyResponse.CopyResponseMeta;
import ru.flashsafe.core.old.storage.rest.data.CreateDirectoryResponse;
import ru.flashsafe.core.old.storage.rest.data.CreateDirectoryResponse.CreateDirectoryResponseMeta;
import ru.flashsafe.core.old.storage.rest.data.DeleteResponse;
import ru.flashsafe.core.old.storage.rest.data.DeleteResponse.DeleteResponseMeta;
import ru.flashsafe.core.old.storage.rest.data.DirectoryListResponse;
import ru.flashsafe.core.old.storage.rest.data.MoveResponse;
import ru.flashsafe.core.old.storage.rest.data.MoveResponse.MoveResponseMeta;
import ru.flashsafe.core.old.storage.rest.data.RenameResponse;
import ru.flashsafe.core.old.storage.rest.data.RenameResponse.RenameResponseMeta;
import ru.flashsafe.core.old.storage.rest.data.ResponseMeta;
import ru.flashsafe.core.operation.OperationIDGenerator;
import ru.flashsafe.core.operation.OperationResult;
import ru.flashsafe.core.operation.OperationState;
import ru.flashsafe.core.operation.monitor.ProcessMonitorInputStream;
import ru.flashsafe.core.storage.SingleFileStorageOperation;
import ru.flashsafe.core.storage.StorageFileOperation;
import ru.flashsafe.core.storage.StorageOperationType;
import ru.flashsafe.core.storage.exception.FlashSafeStorageException;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * 
 * 
 * @author Andrew
 *
 */
@Singleton
public class DefaultFlashSafeStorageService implements FlashSafeStorageIdBasedService {

    private static final int IO_BUFFER_SIZE = 8192;

    private static final String DIRECTORY_API_PATH = "dir"; 

    private static final String DIRECTORY_ID_PARAMETER = "dir_id";
    
    private static final String MOVE_API_PATH = "move.php";
    
    private static final String NEW_DIR_PARAMETER = "new_dir_id";
    
    private static final String COPY_API_PATH = "copy.php";
    
    private static final String RENAME_API_PATH = "rename.php";
    
    private static final String NEW_NAME_PARAMETER = "new_name";
    
    private static final String DELETE_API_PATH = "delete";
    
    private static final String TRASH_API_PATH = "trash";
    
    private static final String PINCODE_PARAMETER = "pincode";

    @SuppressWarnings("unused")
	private static final String FILE_ID_PARAMETER = "file_id";
    
    private static final String FILE_API_PATH = "download";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultFlashSafeStorageService.class);
    
    private final Client restClient;

    private final WebTarget directoryTarget;
    
    private final WebTarget moveTarget;
    
    private final WebTarget copyTarget;
    
    private final WebTarget renameTarget;
    
    private final WebTarget deleteTarget;
    
    private final WebTarget trashTarget;
    
    private final WebTarget fileTarget;
    
    private final ExecutorService executorService = Executors.newFixedThreadPool(FlashSafeRegistry
            .readProperty(FlashSafeRegistry.LOCAL_TO_STORAGE_SIMULTANEOUSLY_EXECUTED_OPERATIONS));

    static {
        System.setProperty("sun.net.http.allowRestrictedHeaders", String.valueOf(true));
        System.setProperty(MessageProperties.IO_BUFFER_SIZE, String.valueOf(IO_BUFFER_SIZE));
    }

    @Inject
    DefaultFlashSafeStorageService(FlashSafeEventService eventService, FlashSafeAuthClientFilter authFilter) {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(JacksonFeature.class).register(authFilter).register(ContentTypeFixerFilter.class).register(new ExternalExecutorProvider(executorService))
                .register(MultiPartFeature.class).register(ProcessMonitorInputStream.class);
        restClient = ClientBuilder.newClient(clientConfig);
        String storageAddress = FlashSafeRegistry.getStorageAddress();
        directoryTarget = restClient.target(storageAddress).path(DIRECTORY_API_PATH);
        moveTarget = restClient.target(storageAddress).path(MOVE_API_PATH);
        copyTarget = restClient.target(storageAddress).path(COPY_API_PATH);
        renameTarget = restClient.target(storageAddress).path(RENAME_API_PATH);
        deleteTarget = restClient.target(storageAddress).path(DELETE_API_PATH);
        trashTarget = restClient.target(storageAddress).path(TRASH_API_PATH);
        fileTarget = restClient.target(storageAddress).path(FILE_API_PATH);
        eventService.registerSubscriber(this);
    }
    
    @Subscribe
    public void handleApplicationStopEvent(ApplicationStopEvent event) {
        LOGGER.info("Handling application stop event");
        restClient.close();
        executorService.shutdownNow();
        try {
            executorService.awaitTermination(500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            LOGGER.error("Shutdown process finished with an error", e);
        }
    }

    @Override
    public List<FlashSafeStorageFileObject> list(long directoryId) throws FlashSafeStorageException {
        return doList(directoryId, null);
    }
    
    @Override
    public List<FlashSafeStorageFileObject> list(long directoryId, String pincode) throws FlashSafeStorageException {    
        if (StringUtils.isBlank(pincode)) {
            throw new IllegalStateException("The pincode should not be null");
        }
        return doList(directoryId, pincode);
     }
    
    private List<FlashSafeStorageFileObject> doList(long directoryId, String pincode) throws FlashSafeStorageException {    
        WebTarget listDirectoryTarger = directoryTarget.queryParam(DIRECTORY_ID_PARAMETER, directoryId);
        if (StringUtils.isNoneBlank(pincode)) {
            listDirectoryTarger = listDirectoryTarger.queryParam(PINCODE_PARAMETER, pincode);
        }
        DirectoryListResponse listResponse = listDirectoryTarger
                .request(MediaType.APPLICATION_JSON_TYPE).get(DirectoryListResponse.class);
        ResponseMeta responseMeta = listResponse.getResponseMeta();
        if (responseMeta.getResponseCode() == HTTP_OK) {
            List<FlashSafeStorageFileObject> fileObjects = listResponse.getFileObjects();
            return fileObjects == null ? Collections.emptyList() : fileObjects;
        }
        throw new FlashSafeStorageException("Error while listing directory. " + responseMeta.getResponseMessage(), null);
    }
    
    @Override
    public List<FlashSafeStorageFileObject> trashList() throws FlashSafeStorageException {
        /*DirectoryListResponse listResponse = trashTarget
                .request(MediaType.APPLICATION_JSON_TYPE).get(DirectoryListResponse.class);
        ResponseMeta responseMeta = listResponse.getResponseMeta();
        if (responseMeta.getResponseCode() == HTTP_OK) {
            List<FlashSafeStorageFileObject> fileObjects = listResponse.getFileObjects();
            return fileObjects == null ? Collections.emptyList() : fileObjects;
        }
        throw new FlashSafeStorageException("Error while listing trash. " + responseMeta.getResponseMessage(), null);*/
    	return doList(-1, null);
    }

    @Override
    public FlashSafeStorageDirectory createDirectory(long parentDirectoryId, String name) throws FlashSafeStorageException {
        Objects.requireNonNull(name);
        Response response = directoryTarget.queryParam(DIRECTORY_ID_PARAMETER, parentDirectoryId)
                .request(MediaType.APPLICATION_JSON_TYPE).post(Entity.form(new Form("create", name)));
        CreateDirectoryResponse createDirectoryRespose = response.readEntity(CreateDirectoryResponse.class);
        CreateDirectoryResponseMeta responseMeta = createDirectoryRespose.getResponseMeta();
        if (responseMeta.getResponseCode() == HTTP_OK) {
            return createLocalRepresentation(responseMeta.getDirectoryId(), name);
        }
        throw new FlashSafeStorageException("Error while creating directory. " + responseMeta.getResponseCode() + " " + responseMeta.getResponseMessage(), null);
    }
    
    @Override
    public FlashSafeStorageFile createEmptyFile(long parentDirectoryId, String name) throws FlashSafeStorageException {
        Objects.requireNonNull(name);
        Response response = directoryTarget.queryParam(DIRECTORY_ID_PARAMETER, parentDirectoryId)
                .request(MediaType.APPLICATION_JSON_TYPE).post(Entity.form(new Form("create_file", name)));
        CreateDirectoryResponse createDirectoryRespose = response.readEntity(CreateDirectoryResponse.class);
        CreateDirectoryResponseMeta responseMeta = createDirectoryRespose.getResponseMeta();
        if (responseMeta.getResponseCode() == HTTP_OK) {
            return createLocalRepresentationForFile(responseMeta.getDirectoryId(), name);
        }
        throw new FlashSafeStorageException("Error while creating directory. " + responseMeta.getResponseCode() + " " + responseMeta.getResponseMessage(), null);
    }

    // TODO get directory attributes from back-end
    private static FlashSafeStorageDirectory createLocalRepresentation(long directoryId, String name) {
        FlashSafeStorageDirectory directory = new FlashSafeStorageDirectory();
        directory.setId(directoryId);
        directory.setName(name);
        return directory;
    }
    
    private static FlashSafeStorageFile createLocalRepresentationForFile(long fileId, String name) {
        FlashSafeStorageFile file = new FlashSafeStorageFile();
        file.setId(fileId);
        file.setName(name);
        return file;
    }

    @Override
    public StorageFileOperation downloadFile(long fileId, Path directory) throws FlashSafeStorageException {
        Objects.requireNonNull(directory);
        final SingleFileStorageOperation operation = createDownloadOperationStatus(OperationIDGenerator.nextId(), directory);
        Future<Response> operationFuture = fileTarget.queryParam("file_id", fileId).request(MediaType.APPLICATION_OCTET_STREAM).async()
                .get(new InvocationCallback<Response>() {
                    
                    @Override
                    public void completed(Response response) {
                        InputStream inputStream = null;
                        OutputStream outputStream = null;
                        try {
                            operation.setTotalBytes(response.getLength());
                            inputStream = new ProcessMonitorInputStream(response.readEntity(InputStream.class), operation);
                            outputStream = new FileOutputStream(directory.toString());
                            IOUtils.copy(inputStream, outputStream);
                            outputStream.flush();
                            setStatusToFinished(operation, OperationResult.SUCCESS);
                        } catch (IOException ex) {
                            setStatusToFinished(operation, OperationResult.ERROR);
                            LOGGER.error("Error while downloading file with id " + fileId, ex);
                        } finally {
                            IOUtils.closeQuietly(inputStream);
                            IOUtils.closeQuietly(outputStream);
                            response.close();
                        }
                    }

                    @Override
                    public void failed(Throwable throwable) {
                        if (throwable instanceof CancellationException) {
                            setStatusToFinished(operation, OperationResult.CANCELED);
                            LOGGER.debug("Downloading of {} to {} was canceled", fileId, directory);
                        } else {
                            setStatusToFinished(operation, OperationResult.ERROR);
                            LOGGER.warn("Error while downloading file with id " + fileId + " to directory " + directory, throwable);
                        }
                    }

                });
        operation.setOperationFuture(operationFuture);
        return operation;
    }

    @Override
    public StorageFileOperation uploadFile(long directoryId, Path file) throws FlashSafeStorageException {
        Objects.requireNonNull(file);
        final SingleFileStorageOperation operation = createUploadOperationStatus(OperationIDGenerator.nextId(), file);

        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
        multiPart.field(DIRECTORY_ID_PARAMETER, Long.toString(directoryId));
        StreamDataBodyPart bp = buildStreamDataBodyPart(file, operation);
        multiPart.bodyPart(bp);
        Future<Response> operationFuture = directoryTarget.request(MediaType.APPLICATION_JSON_TYPE).async()
                .post(Entity.entity(multiPart, multiPart.getMediaType()), new InvocationCallback<Response>() {

                    @Override
                    public void completed(Response response) {
                        response.close();
                        setStatusToFinished(operation, OperationResult.SUCCESS);
                    }

                    @Override
                    public void failed(Throwable throwable) {
                        if (throwable instanceof CancellationException) {
                            setStatusToFinished(operation, OperationResult.CANCELED);
                            LOGGER.debug("Uploading of {} to {} was canceled", file, directoryId);
                        } else {
                            setStatusToFinished(operation, OperationResult.ERROR);
                            LOGGER.warn("Error while uploading file " + file + " to directory with id " + directoryId, throwable);
                        }
                    }

                });
        operation.setOperationFuture(operationFuture);
        return operation;
    }

    @Override
    public void copy(long fileObjectId, long destinationDirectoryId) throws FlashSafeStorageException {
    	Objects.requireNonNull(fileObjectId);
    	Objects.requireNonNull(destinationDirectoryId);
        Response response = copyTarget.queryParam(FILE_ID_PARAMETER, fileObjectId).queryParam(NEW_DIR_PARAMETER, destinationDirectoryId)
                .request(MediaType.APPLICATION_JSON_TYPE).get();
        CopyResponse copyRespose = response.readEntity(CopyResponse.class);
        CopyResponseMeta responseMeta = copyRespose.getResponseMeta();
        if (responseMeta.getResponseCode() != HTTP_OK) {
        	throw new FlashSafeStorageException("Error while copy object. " + responseMeta.getResponseMessage(), null);
        }
    }

    @Override
    public void move(long fileObjectId, long destinationDirectoryId) throws FlashSafeStorageException {
    	Objects.requireNonNull(fileObjectId);
    	Objects.requireNonNull(destinationDirectoryId);
        Response response = moveTarget.queryParam(FILE_ID_PARAMETER, fileObjectId).queryParam(NEW_DIR_PARAMETER, destinationDirectoryId)
                .request(MediaType.APPLICATION_JSON_TYPE).get();
        MoveResponse moveRespose = response.readEntity(MoveResponse.class);
        MoveResponseMeta responseMeta = moveRespose.getResponseMeta();
        if (responseMeta.getResponseCode() != HTTP_OK) {
        	throw new FlashSafeStorageException("Error while move object. " + responseMeta.getResponseMessage(), null);
        }
    }

    @Override
    public void delete(long fileObjectId) throws FlashSafeStorageException {
    	Objects.requireNonNull(fileObjectId);
        Response response = deleteTarget.queryParam(FILE_ID_PARAMETER, fileObjectId)
                .request(MediaType.APPLICATION_JSON_TYPE).get();
        DeleteResponse deleteRespose = response.readEntity(DeleteResponse.class);
        DeleteResponseMeta responseMeta = deleteRespose.getResponseMeta();
        if (responseMeta.getResponseCode() != HTTP_OK) {
        	throw new FlashSafeStorageException("Error while delete object. " + responseMeta.getResponseMessage(), null);
        }
    }
    
    @Override
    public void rename(long fileObjectId, String name) throws FlashSafeStorageException {
    	Objects.requireNonNull(fileObjectId);
    	Objects.requireNonNull(name);
        Response response = renameTarget.queryParam(FILE_ID_PARAMETER, fileObjectId).queryParam(NEW_NAME_PARAMETER, name)
                .request(MediaType.APPLICATION_JSON_TYPE).get();
        RenameResponse moveRespose = response.readEntity(RenameResponse.class);
        RenameResponseMeta responseMeta = moveRespose.getResponseMeta();
        if (responseMeta.getResponseCode() != HTTP_OK) {
        	throw new FlashSafeStorageException("Error while rename object. " + responseMeta.getResponseMessage(), null);
        }
    }

    private static void setStatusToFinished(SingleFileStorageOperation operation, OperationResult result) {
        operation.setResult(result);
        operation.setState(OperationState.FINISHED);
        operation.markAsFinished();
    }

    private static StreamDataBodyPart buildStreamDataBodyPart(Path fileToSend, SingleFileStorageOperation operation)
            throws FlashSafeStorageException {
        try {
            InputStream fileInStream = new ProcessMonitorInputStream(new BufferedInputStream(new FileInputStream(
                    fileToSend.toFile())), operation);
            return new StreamDataBodyPart("file", fileInStream, fileToSend.toFile().getName());
        } catch (FileNotFoundException e) {
            // TODO add message
            throw new FlashSafeStorageException("", e);
        }
    }

    private static SingleFileStorageOperation createUploadOperationStatus(long operationId, Path file)
            throws FlashSafeStorageException {
        FileOperationInfo operationInfo = new FileOperationInfo(file.toString(), null, file.getFileName().toString());
        try {
            return new SingleFileStorageOperation(OperationIDGenerator.nextId(), null, StorageOperationType.UPLOAD,
                    operationInfo, Files.size(file));
        } catch (IOException e) {
            throw new FlashSafeStorageException("Error while calculating file size", e);
        }
    }
    
    private static SingleFileStorageOperation createDownloadOperationStatus(long operationId, Path file)
            throws FlashSafeStorageException {
        FileOperationInfo operationInfo = new FileOperationInfo(file.toString(), null, file.getFileName().toString());
        return new SingleFileStorageOperation(OperationIDGenerator.nextId(), null, StorageOperationType.DOWNLOAD, operationInfo);
    }

}
