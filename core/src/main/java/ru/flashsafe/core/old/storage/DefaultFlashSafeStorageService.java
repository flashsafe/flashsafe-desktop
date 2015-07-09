package ru.flashsafe.core.old.storage;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;
import org.glassfish.jersey.message.internal.ReaderWriter;

import com.sun.xml.internal.org.jvnet.staxex.StreamingDataHandler;

import ru.flashsafe.core.old.storage.rest.ContentTypeFixerFilter;
import ru.flashsafe.core.old.storage.rest.FlashSafeAuthFeature;
import ru.flashsafe.core.old.storage.rest.data.CreateDirectoryResponse;
import ru.flashsafe.core.old.storage.rest.data.CreateDirectoryResponse.CreateDirectoryResponseMeta;
import ru.flashsafe.core.old.storage.rest.data.DirectoryListResponse;
import ru.flashsafe.core.old.storage.rest.data.ResponseMeta;
import ru.flashsafe.core.operation.OperationState;
import ru.flashsafe.core.operation.ProcessIDGenerator;
import ru.flashsafe.core.storage.FlashSafeStorageService;
import ru.flashsafe.core.storage.StorageOperationStatus;
import ru.flashsafe.core.storage.StorageOperationStatusImpl;
import ru.flashsafe.core.storage.StorageOperationType;
import ru.flashsafe.core.storage.exception.FlashSafeStorageException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class DefaultFlashSafeStorageService implements FlashSafeStorageService {

    /* "http://localhost:8088/hackathon-webapi/api/reviewee-request/" */
    private static final String STORAGE_API_URL = "https://flashsafe-alpha.azurewebsites.net";

    private static final String DIRECTORY_API_PATH = "dir.php";

    private static final String DIRECTORY_ID_PARAMETER = "dir_id";

    private final Client restClient;

    private final WebTarget directoryTarget;

    public DefaultFlashSafeStorageService() {
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(JacksonFeature.class).register(FlashSafeAuthFeature.class).register(ContentTypeFixerFilter.class)
                .register(MultiPartFeature.class).property(HttpUrlConnectorProvider.USE_FIXED_LENGTH_STREAMING, false)
                .property(ClientProperties.OUTBOUND_CONTENT_LENGTH_BUFFER, 0)
                .register(new LoggingFilter(Logger.getLogger("com.example.app"), true)).register(ProcessMonitorInputStream.class);
        restClient = ClientBuilder.newClient(clientConfig);
        directoryTarget = restClient.target(STORAGE_API_URL).path(DIRECTORY_API_PATH);
    }

    @Override
    public List<FlashSafeStorageFileObject> list(long directoryId) throws FlashSafeStorageException {
        DirectoryListResponse listResponse = directoryTarget.queryParam(DIRECTORY_ID_PARAMETER, directoryId)
                .request(MediaType.APPLICATION_JSON_TYPE).get(DirectoryListResponse.class);
        ResponseMeta responseMeta = listResponse.getResponseMeta();
        if (responseMeta.getResponseCode() == 200) {
            return listResponse.getFileObjects();
        }
        throw new FlashSafeStorageException("Error while listing directory. " + responseMeta.getResponseMessage(), null);
    }

    @Override
    public FlashSafeStorageDirectory createDirectory(long parentDirectoryId, String name) throws FlashSafeStorageException {
        Response response = directoryTarget.queryParam(DIRECTORY_ID_PARAMETER, parentDirectoryId)
                .request(MediaType.APPLICATION_JSON_TYPE).post(Entity.form(new Form("create", name)));
        CreateDirectoryResponse createDirectoryRespose = response.readEntity(CreateDirectoryResponse.class);
        CreateDirectoryResponseMeta responseMeta = createDirectoryRespose.getResponseMeta();
        if (responseMeta.getResponseCode() == 200) {
            return createLocalRepresentation(responseMeta.getDirectoryId(), name);
        }
        throw new FlashSafeStorageException("Error while creating directory. " + responseMeta.getResponseMessage(), null);
    }

    // TODO get directory attributes from back-end
    private static FlashSafeStorageDirectory createLocalRepresentation(long directoryId, String name) {
        FlashSafeStorageDirectory directory = new FlashSafeStorageDirectory();
        return directory;
    }

    @Override
    public StorageOperationStatus downloadFile(long fileId, Path directory) {
        // TODO Auto-generated method stub
        throw new NotImplementedException();
    }

    @Override
    public StorageOperationStatus uploadFile(long directoryId, Path file) throws FlashSafeStorageException {
        final StorageOperationStatusImpl operationStatus = createOperationStatus(ProcessIDGenerator.nextId(), file);

        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
        multiPart.field(DIRECTORY_ID_PARAMETER, Long.toString(directoryId));

        try {
            // TODO fix improve buffer
            InputStream fileInStream = new ProcessMonitorInputStream(new BufferedInputStream(new FileInputStream(file.toFile()),
                    81920), operationStatus);
            StreamDataBodyPart bp = new StreamDataBodyPart("file", fileInStream, file.toFile().getName());
            
            multiPart.bodyPart(bp);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        
        directoryTarget.request(MediaType.APPLICATION_JSON_TYPE).async()
                .post(Entity.entity(multiPart, multiPart.getMediaType()), new InvocationCallback<Response>() {

                    @Override
                    public void completed(Response response) {
                        setStatusToFinished(operationStatus);
                    }

                    @Override
                    public void failed(Throwable throwable) {
                        setStatusToFinished(operationStatus);
                    }

                });
        return operationStatus;
    }

    @Override
    public void copy(long fileObjectId, long destinationDirectoryId) {
        // TODO Auto-generated method stub
        throw new NotImplementedException();
    }

    @Override
    public void move(long fileObjectId, long destinationDirectoryId) {
        // TODO Auto-generated method stub
        throw new NotImplementedException();
    }

    @Override
    public void delete(long fileObjectId) {
        // TODO Auto-generated method stub
        throw new NotImplementedException();
    }

    private static void setStatusToFinished(StorageOperationStatusImpl operationStatus) {
        operationStatus.setState(OperationState.FINISHED);
        operationStatus.markAsFinished();
    }

    private static StorageOperationStatusImpl createOperationStatus(long operationId, Path file) throws FlashSafeStorageException {
        try {
            return new StorageOperationStatusImpl(ProcessIDGenerator.nextId(), StorageOperationType.UPLOAD, Files.size(file));
        } catch (IOException e) {
            throw new FlashSafeStorageException("Error while calculating file size", e);
        }
    }

    public class ProcessMonitorInputStream extends InputStream {

        private final InputStream inputStream;

        private final StorageOperationStatusImpl fileOperationStatus;

        public ProcessMonitorInputStream(InputStream inputStream, StorageOperationStatusImpl fileOperationStatus) {
            this.inputStream = inputStream;
            this.fileOperationStatus = fileOperationStatus;
        }

        @Override
        public int available() throws IOException {
            return inputStream.available();
        }

        @Override
        public void mark(int readlimit) {
            inputStream.mark(readlimit);
            // TODO apply changes to counter;
        }

        @Override
        public boolean markSupported() {
            return inputStream.markSupported();
        }

        @Override
        public int read() throws IOException {
            int byteData = inputStream.read();
            fileOperationStatus.incrementProcessedBytes(1);
            return byteData;
        }

        @Override
        public int read(byte[] data) throws IOException {
            int byteCount = inputStream.read(data);
            if (byteCount > 0) {
                fileOperationStatus.incrementProcessedBytes(byteCount);
            }
            return byteCount;
        }

        @Override
        public int read(byte[] data, int offset, int length) throws IOException {
            int byteCount = inputStream.read(data, offset, length);
            if (byteCount > 0) {
                fileOperationStatus.incrementProcessedBytes(byteCount);
            }
            return byteCount;
        }

        @Override
        public void reset() throws IOException {
            inputStream.reset();
            // TODO apply reset to counter;
        }

        public long skip(long length) throws IOException {
            long byteCount = inputStream.skip(length);
            if (byteCount > 0) {
                fileOperationStatus.incrementProcessedBytes(byteCount);
            }
            return byteCount;
        }

        public void close() throws IOException {
            inputStream.close();
        }

    }

}
