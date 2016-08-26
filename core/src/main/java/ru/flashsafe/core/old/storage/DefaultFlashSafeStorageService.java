package ru.flashsafe.core.old.storage;

import static java.net.HttpURLConnection.HTTP_OK;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
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
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
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
import ru.flashsafe.core.old.storage.rest.CustomMultipart;
import ru.flashsafe.core.old.storage.rest.ExternalExecutorProvider;
import ru.flashsafe.core.old.storage.rest.FlashSafeAuthClientFilter;
import ru.flashsafe.core.old.storage.rest.data.CopyResponse;
import ru.flashsafe.core.old.storage.rest.data.CopyResponse.CopyResponseMeta;
import ru.flashsafe.core.old.storage.rest.data.CreateDirectoryResponse;
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
import java.io.ByteArrayInputStream;
import javax.ws.rs.client.Invocation.Builder;
import org.glassfish.jersey.client.ClientProperties;
import ru.flashsafe.core.file.Directory;
import ru.flashsafe.core.file.FileObjectType;
import ru.flashsafe.core.old.storage.rest.data.TreeResponse;
import ru.flashsafe.core.old.storage.rest.data.UploadResponse;

/**
 * 
 * 
 * @author Andrew
 *
 */
@SuppressWarnings("deprecation")
@Singleton
public class DefaultFlashSafeStorageService implements FlashSafeStorageIdBasedService {

    private static final int IO_BUFFER_SIZE = 8192;

    private static final String DIRECTORY_API_PATH = "dir.php"; 

    private static final String DIRECTORY_ID_PARAMETER = "dir_id";
    
    private static final String MOVE_API_PATH = "move.php";
    
    private static final String NEW_DIR_PARAMETER = "new_dir_id";
    
    private static final String COPY_API_PATH = "copy.php";
    
    private static final String RENAME_API_PATH = "rename.php";
    
    private static final String NEW_NAME_PARAMETER = "new_name";
    
    private static final String DELETE_API_PATH = "delete.php";
    
    private static final String TRASH_API_PATH = "trash.php";
    
    private static final String PINCODE_PARAMETER = "pincode";

    @SuppressWarnings("unused")
	private static final String FILE_ID_PARAMETER = "file_id";
    
    private static final String FILE_API_PATH = "getFile.php";
    
    private static final String API_PATH = "api.php";
    
    private static final String PARENT_PARAMETER = "parent";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultFlashSafeStorageService.class);
    
    private final Client restClient;

    public final WebTarget directoryTarget;
    
    private final WebTarget moveTarget;
    
    private final WebTarget copyTarget;
    
    private final WebTarget renameTarget;
    
    private final WebTarget deleteTarget;
    
    private final WebTarget trashTarget;
    
    private final WebTarget fileTarget;
    
    private final WebTarget apiTarget;
    
    private final ExecutorService executorService = Executors.newFixedThreadPool(FlashSafeRegistry
            .readProperty(FlashSafeRegistry.LOCAL_TO_STORAGE_SIMULTANEOUSLY_EXECUTED_OPERATIONS));
    
    private final DefaultFlashSafeStorageService self = this;
    
    private Future future;

    static {
        System.setProperty("sun.net.http.allowRestrictedHeaders", String.valueOf(true));
        System.setProperty(MessageProperties.IO_BUFFER_SIZE, String.valueOf(IO_BUFFER_SIZE));
    }

    @Inject
    DefaultFlashSafeStorageService(FlashSafeEventService eventService, FlashSafeAuthClientFilter authFilter) {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(JacksonFeature.class)/*.register(authFilter)*/.register(ContentTypeFixerFilter.class).register(new ExternalExecutorProvider(executorService))
                .register(MultiPartFeature.class).register(CustomMultipart.class).register(ProcessMonitorInputStream.class)
//                 .register(new
//                 LoggingFilter(java.util.logging.Logger.getLogger(DefaultFlashSafeStorageService.class.getName()),
//                 true))
                .property(HttpUrlConnectorProvider.USE_FIXED_LENGTH_STREAMING, true);
        restClient = ClientBuilder.newClient(clientConfig);
        String storageAddress = FlashSafeRegistry.getStorageAddress();
        directoryTarget = restClient.target(storageAddress).path(DIRECTORY_API_PATH);
        moveTarget = restClient.target(storageAddress).path(MOVE_API_PATH);
        copyTarget = restClient.target(storageAddress).path(COPY_API_PATH);
        renameTarget = restClient.target(storageAddress).path(RENAME_API_PATH);
        deleteTarget = restClient.target(storageAddress).path(DELETE_API_PATH);
        trashTarget = restClient.target(storageAddress).path(TRASH_API_PATH);
        fileTarget = restClient.target(storageAddress).path(FILE_API_PATH);
        apiTarget = restClient.target(storageAddress).path(API_PATH);
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
    public List<FlashSafeStorageFileObject> list(String directoryHash) throws FlashSafeStorageException {
        return doList(directoryHash, null);
    }
    
    @Override
    public List<FlashSafeStorageFileObject> list(String directoryHash, String pincode) throws FlashSafeStorageException {    
        if (StringUtils.isBlank(pincode)) {
            throw new IllegalStateException("The pincode should not be null");
        }
        return doList(directoryHash, pincode);
     }
    
    private List<FlashSafeStorageFileObject> doList(String directoryHash, String pincode) throws FlashSafeStorageException {    
        WebTarget listDirectoryTarger = apiTarget.queryParam("method","ListObjects")
                .queryParam("dsn", "1")
                .queryParam("token", "123456")
                .queryParam(PARENT_PARAMETER, directoryHash);
        DirectoryListResponse listResponse = listDirectoryTarger
                .request().get(DirectoryListResponse.class);
        if (listResponse.getStatus().equals("success")) {
            List<FlashSafeStorageFileObject> fileObjects = listResponse.getFileObjects();
            return fileObjects == null ? Collections.emptyList() : fileObjects;
        }
        throw new FlashSafeStorageException("Error while listing directory. " + listResponse.getResult(), null);
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
    	return doList("", null);
    }

    @Override
    public FlashSafeStorageDirectory createDirectory(String parentDirectoryHash, String name) throws FlashSafeStorageException {
        Objects.requireNonNull(name);
        Response response = apiTarget.queryParam("method", "MakeFolder")
                .queryParam("dsn", "1")
                .queryParam("token", "123456")
                .queryParam("parent", parentDirectoryHash)
                .queryParam("folderName", name)
                .request().get();
        CreateDirectoryResponse createDirectoryResponse = response.readEntity(CreateDirectoryResponse.class);
        if (createDirectoryResponse.getStatus().equals("success")) {
            FlashSafeStorageDirectory dir = new FlashSafeStorageDirectory();
            dir.setObjectHash(createDirectoryResponse.getHash());
            dir.setParentHash(parentDirectoryHash);
            dir.setType(FileObjectType.DIRECTORY);
            dir.setName(name);
            dir.setExt("");
            dir.setSize(0);
            dir.setMimeType("");
            return dir;
        }
        throw new FlashSafeStorageException("Error while creating directory. " + createDirectoryResponse.getResult(), null);
    }
    
    @Override
    public FlashSafeStorageFile createEmptyFile(String parentDirectoryHash, String name) throws FlashSafeStorageException {
        Objects.requireNonNull(name);
        Response response = directoryTarget.queryParam(DIRECTORY_ID_PARAMETER, parentDirectoryHash)
                .request(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(Entity.form(new Form("create_file", name)));
        CreateDirectoryResponse createDirectoryResponse = response.readEntity(CreateDirectoryResponse.class);
        //CreateDirectoryResponseMeta responseMeta = createDirectoryRespose.getResponseMeta();
        //if (responseMeta.getResponseCode() == HTTP_OK) {
            //return createLocalRepresentationForFile(responseMeta.getFileHash(), name);
        //}
        //throw new FlashSafeStorageException("Error while creating directory. " + responseMeta.getResponseCode() + " " + responseMeta.getResponseMessage(), null);
        return null;
    }

    // TODO get directory attributes from back-end
    private static FlashSafeStorageDirectory createLocalRepresentation(String directoryHash, String name) {
        FlashSafeStorageDirectory directory = new FlashSafeStorageDirectory();
        directory.setObjectHash(directoryHash);
        directory.setName(name);
        return directory;
    }
    
    private static FlashSafeStorageFile createLocalRepresentationForFile(String fileHash, String name) {
        FlashSafeStorageFile file = new FlashSafeStorageFile();
        file.setObjectHash(fileHash);
        file.setName(name);
        return file;
    }

    @Override
    public StorageFileOperation downloadFile(String fileHash, Path directory) throws FlashSafeStorageException {
        Objects.requireNonNull(directory);
        final SingleFileStorageOperation operation = createDownloadOperationStatus(OperationIDGenerator.nextId(), directory);
        Future<Response> operationFuture = fileTarget.queryParam("dsn", "1")
                .queryParam("token", "123456")
                .queryParam("hash", fileHash)
                .request().async()
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
                            LOGGER.error("Error while downloading file with id " + fileHash, ex);
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
                            LOGGER.debug("Downloading of {} to {} was canceled", fileHash, directory);
                        } else {
                            setStatusToFinished(operation, OperationResult.ERROR);
                            LOGGER.warn("Error while downloading file with hash " + fileHash + " to directory " + directory, throwable);
                        }
                    }

                });
        operation.setOperationFuture(operationFuture);
        return operation;
    }

    @Override
    public StorageFileOperation uploadFile(final String directoryHash, final Path file) throws FlashSafeStorageException {
        Objects.requireNonNull(file);
        final SingleFileStorageOperation operation = createUploadOperationStatus(OperationIDGenerator.nextId(), file);

        try {
            //if(Files.size(file) <= 512 * 1024) {
                FormDataMultiPart multiPart = new FormDataMultiPart();
                multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
                //multiPart.field("access_token", FlashSafeAuthClientFilter.getAuthData().getToken());
                multiPart.field("method", "Upload");
                multiPart.field("dsn", "1");
                multiPart.field("token", "123456");
                multiPart.field("fileSize", String.valueOf(file.toFile().length()));
                multiPart.field("parent", directoryHash);
                //multiPart.field("file_size", String.valueOf(file.toFile().length()));
                StreamDataBodyPart bp = buildStreamDataBodyPart(file, operation);
                multiPart.bodyPart(bp);
                /* Achtung! - black magic was used */
                long length = /*139 + 84 + String.valueOf(file.toFile().length()).length() +*/ calculateContentLength(directoryHash, file, false);
                future = apiTarget.request()
                        .async()
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
                                    LOGGER.debug("Uploading of {} to {} was canceled", file, directoryHash);
                                } else {
                                    setStatusToFinished(operation, OperationResult.ERROR);
                                    LOGGER.warn("Error while uploading file " + file + " to directory with id " + directoryHash, throwable);
                                }
                            }

                });
            //} else {
                //future = new UploadFileFuture(directoryId, -1, file, self, operation);
            //}
        } catch(/*IOException | */FlashSafeStorageException e) {
            LOGGER.error("Error while uploading file: " + file.getFileName(), e);
        }
        operation.setOperationFuture(future);
        if(future instanceof UploadFileFuture) ((UploadFileFuture) future).run();
        return operation;
    }
    
    @Override
    public StorageFileOperation uploadFilePart(String directoryHash, String fhash, long part_num, Path file) throws FlashSafeStorageException {
        Objects.requireNonNull(file);
        final SingleFileStorageOperation operation = createUploadOperationStatus(OperationIDGenerator.nextId(), file);

        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
        multiPart.field("access_token", FlashSafeAuthClientFilter.getAuthData().getToken());
        multiPart.field(DIRECTORY_ID_PARAMETER, directoryHash);
        multiPart.field(FILE_ID_PARAMETER, fhash);
        StreamDataBodyPart bp = buildStreamDataBodyPart(file, part_num, operation);
        multiPart.bodyPart(bp);
        /* Achtung! - black magic was used */
        long length = 139 + 106 + calculateContentLength(directoryHash, file, true);
        
        Future<UploadResponse> operationFuture = directoryTarget.request(MediaType.APPLICATION_JSON_TYPE).header(HttpHeaders.CONTENT_LENGTH, length).async()
                .post(Entity.entity(multiPart, multiPart.getMediaType()), new InvocationCallback<UploadResponse>() {

                    @Override
                    public void completed(UploadResponse response) {
                        String fhash = response.getResponseMeta().getFileHash();
                        setStatusToFinished(operation, OperationResult.SUCCESS);
                    }

                    @Override
                    public void failed(Throwable throwable) {
                        if (throwable instanceof CancellationException) {
                            setStatusToFinished(operation, OperationResult.CANCELED);
                            LOGGER.debug("Uploading of {} to {} was canceled", file, directoryHash);
                        } else {
                            setStatusToFinished(operation, OperationResult.ERROR);
                            LOGGER.warn("Error while uploading file " + file + " to directory with id " + directoryHash, throwable);
                        }
                    }

                });
        operation.setOperationFuture(operationFuture);
        return operation;
    }

    @Override
    public void copy(String fileObjectHash, String destinationDirectoryHash) throws FlashSafeStorageException {
    	Objects.requireNonNull(fileObjectHash);
    	Objects.requireNonNull(destinationDirectoryHash);
        Response response = copyTarget.queryParam(FILE_ID_PARAMETER, fileObjectHash).queryParam(NEW_DIR_PARAMETER, destinationDirectoryHash)
                .request(MediaType.APPLICATION_JSON_TYPE).get();
        CopyResponse copyRespose = response.readEntity(CopyResponse.class);
        CopyResponseMeta responseMeta = copyRespose.getResponseMeta();
        if (responseMeta.getResponseCode() != HTTP_OK) {
        	throw new FlashSafeStorageException("Error while copy object. " + responseMeta.getResponseMessage(), null);
        }
    }

    @Override
    public void move(String fileObjectHash, String destinationDirectoryHash) throws FlashSafeStorageException {
    	Objects.requireNonNull(fileObjectHash);
    	Objects.requireNonNull(destinationDirectoryHash);
        Response response = moveTarget.queryParam(FILE_ID_PARAMETER, fileObjectHash).queryParam(NEW_DIR_PARAMETER, destinationDirectoryHash)
                .request(MediaType.APPLICATION_JSON_TYPE).get();
        MoveResponse moveRespose = response.readEntity(MoveResponse.class);
        MoveResponseMeta responseMeta = moveRespose.getResponseMeta();
        if (responseMeta.getResponseCode() != HTTP_OK) {
        	throw new FlashSafeStorageException("Error while move object. " + responseMeta.getResponseMessage(), null);
        }
    }

    @Override
    public void delete(String fileObjectHash) throws FlashSafeStorageException {
    	Objects.requireNonNull(fileObjectHash);
        Response response = deleteTarget.queryParam(FILE_ID_PARAMETER, fileObjectHash)
                .request(MediaType.APPLICATION_JSON_TYPE).get();
        DeleteResponse deleteRespose = response.readEntity(DeleteResponse.class);
        DeleteResponseMeta responseMeta = deleteRespose.getResponseMeta();
        if (responseMeta.getResponseCode() != HTTP_OK) {
        	throw new FlashSafeStorageException("Error while delete object. " + responseMeta.getResponseMessage(), null);
        }
    }
    
    @Override
    public void rename(String fileObjectHash, String name) throws FlashSafeStorageException {
    	Objects.requireNonNull(fileObjectHash);
    	Objects.requireNonNull(name);
        Response response = renameTarget.queryParam(FILE_ID_PARAMETER, fileObjectHash).queryParam(NEW_NAME_PARAMETER, name)
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
    
    private static StreamDataBodyPart buildStreamDataBodyPart(Path fileToSend, long part_num, SingleFileStorageOperation operation)
            throws FlashSafeStorageException {
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileToSend.toFile()));
            bis.skip(512 * 1024 * (part_num - 1));
            byte[] part = new byte[512 * 1024];
            bis.read(part);
            bis.close();
            BufferedInputStream bis1 = new BufferedInputStream(new ByteArrayInputStream(part));
            InputStream fileInStream = new ProcessMonitorInputStream(bis1, operation);
            return new StreamDataBodyPart("file", fileInStream, fileToSend.toFile().getName());
        } catch (IOException e) {
            // TODO add message
            throw new FlashSafeStorageException("", e);
        }
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
    public static long calculateContentLength(String directoryHash, Path fileToLoad, boolean isPart) throws FlashSafeStorageException {
        Objects.requireNonNull(fileToLoad);
        long length = 213;
        String boundaryString = CustomMultipart.generateBoundary();
        length += boundaryString.length() * /*3*/2;
        length += directoryHash.length();
        length += fileToLoad.toFile().getName().getBytes(StandardCharsets.UTF_8).length;
        //long partLength = 512 * 1024;
        length += fileToLoad.toFile().length();
        return length;
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

    @Override
    public List<FlashSafeStorageFileObject> getTree() throws FlashSafeStorageException {
        Builder builder = apiTarget.queryParam("method","GetTree")
                .queryParam("dsn", "1")
                .queryParam("token", "123456")
                .request();
        TreeResponse response = builder.get(TreeResponse.class);
        return response.getFileObjects();
    }

}
