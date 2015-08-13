package ru.flashsafe.core.old.storage;

import static java.net.HttpURLConnection.HTTP_OK;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Permission;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;
import org.glassfish.jersey.message.MessageProperties;

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

    private static final String STORAGE_API_URL = "https://flashsafe-alpha.azurewebsites.net";
    
    private static final int IO_BUFFER_SIZE = 8192;

    private static final String DIRECTORY_API_PATH = "dir.php";

    private static final String DIRECTORY_ID_PARAMETER = "dir_id";

    private final Client restClient;

    private final WebTarget directoryTarget;
    
    static {
        System.setProperty("sun.net.http.allowRestrictedHeaders", String.valueOf(Boolean.TRUE));
        System.setProperty(MessageProperties.IO_BUFFER_SIZE, String.valueOf(IO_BUFFER_SIZE));
    }

    public DefaultFlashSafeStorageService() {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(JacksonFeature.class).register(FlashSafeAuthFeature.class).register(ContentTypeFixerFilter.class)
                .register(MultiPartFeature.class)
                .register(ProcessMonitorInputStream.class)
               // .register(new LoggingFilter(Logger.getLogger(DefaultFlashSafeStorageService.class.getName()), true))
                .property(HttpUrlConnectorProvider.USE_FIXED_LENGTH_STREAMING, true)
                .property(ClientProperties.OUTBOUND_CONTENT_LENGTH_BUFFER, 0);
        HttpUrlConnectorProvider httpUrlConnectorProvider = new HttpUrlConnectorProvider();
        httpUrlConnectorProvider.connectionFactory(new HttpUrlConnectorProvider.ConnectionFactory() {
            
            @Override
            public HttpURLConnection getConnection(URL url) throws IOException {
                HttpURLConnection connection = new HttpURLConnectionWrapper((HttpURLConnection) url.openConnection());
                return connection;
            }
        });
        //clientConfig.connectorProvider(httpUrlConnectorProvider);
        restClient = ClientBuilder.newClient(clientConfig);
        directoryTarget = restClient.target(STORAGE_API_URL).path(DIRECTORY_API_PATH);
    }

    @Override
    public List<FlashSafeStorageFileObject> list(long directoryId) throws FlashSafeStorageException {
        DirectoryListResponse listResponse = directoryTarget.queryParam(DIRECTORY_ID_PARAMETER, directoryId)
                .request(MediaType.APPLICATION_JSON_TYPE).get(DirectoryListResponse.class);
        ResponseMeta responseMeta = listResponse.getResponseMeta();
        if (responseMeta.getResponseCode() == HTTP_OK) {
            return listResponse.getFileObjects();
        }
        throw new FlashSafeStorageException("Error while listing directory. " + responseMeta.getResponseMessage(), null);
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
        throw new FlashSafeStorageException("Error while creating directory. " + responseMeta.getResponseMessage(), null);
    }

    // TODO get directory attributes from back-end
    private static FlashSafeStorageDirectory createLocalRepresentation(long directoryId, String name) {
        FlashSafeStorageDirectory directory = new FlashSafeStorageDirectory();
        directory.setId(directoryId);
        directory.setName(name);
        return directory;
    }

    @Override
    public StorageOperationStatus downloadFile(long fileId, Path directory) {
        // TODO Auto-generated method stub
        throw new NotImplementedException();
    }

    @Override
    public StorageOperationStatus uploadFile(long directoryId, Path file) throws FlashSafeStorageException {
        Objects.requireNonNull(file);
        final StorageOperationStatusImpl operationStatus = createOperationStatus(ProcessIDGenerator.nextId(), file);

        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
        multiPart.field(DIRECTORY_ID_PARAMETER, Long.toString(directoryId));
        StreamDataBodyPart bp = buildStreamDataBodyPart(file, operationStatus);
        multiPart.bodyPart(bp);

        /* Achtung! - black magic was used */
        long length = calculateContentLength(directoryId, file);
        directoryTarget.request(MediaType.APPLICATION_JSON_TYPE).header("Content-Length", length).async()
                .post(Entity.entity(multiPart, multiPart.getMediaType()), new InvocationCallback<Response>() {

                    @Override
                    public void completed(Response response) {
                        setStatusToFinished(operationStatus);
                    }

                    @Override
                    public void failed(Throwable throwable) {
                        throwable.printStackTrace();
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
    
    private static StreamDataBodyPart buildStreamDataBodyPart(Path fileToSend, StorageOperationStatusImpl operationStatus) throws FlashSafeStorageException {
        try {
            InputStream fileInStream = new ProcessMonitorInputStream(new BufferedInputStream(new FileInputStream(fileToSend.toFile())), operationStatus);
            return new StreamDataBodyPart("file", fileInStream, fileToSend.toFile().getName());
        } catch (FileNotFoundException e) {
            //TODO add message
            throw new FlashSafeStorageException("", e);
        }
    }
    
    /**
     * Calculates content-length using a lot of calculated guesses..
     * 
     * 1. We send FormDataMultiPart which consists of 2 parts:
     *    - directory Id field,
     *    - file stream body part.
     *    295 is a number of bytes produces by Jersey for these 2 parts.
     *    The we add directoryId length;
     *    Then we add file name - it can be different...
     *    Then the file length. 
     * 
     * @param directoryId
     * @param fileToLoad
     * @return
     * @throws FlashSafeStorageException 
     * @throws UnsupportedEncodingException 
     */
    private static long calculateContentLength(long directoryId, Path fileToLoad) throws FlashSafeStorageException {
        Objects.requireNonNull(fileToLoad);
        long length = 295;
        length += String.valueOf(directoryId).length();
        try {
            length += fileToLoad.toFile().getName().length();
            length += fileToLoad.toFile().length();
            return length;
        } catch (RuntimeException e) {
            throw new FlashSafeStorageException("", e);
        }

    }

    private static StorageOperationStatusImpl createOperationStatus(long operationId, Path file) throws FlashSafeStorageException {
        try {
            return new StorageOperationStatusImpl(ProcessIDGenerator.nextId(), StorageOperationType.UPLOAD, Files.size(file));
        } catch (IOException e) {
            throw new FlashSafeStorageException("Error while calculating file size", e);
        }
    }

    public static class ProcessMonitorInputStream extends InputStream {

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
    
    public static class HttpURLConnectionWrapper extends HttpURLConnection {

        private HttpURLConnection httpURLConnection;
        
        private OutputStream outputStream;
        
        private long written;
        
        protected HttpURLConnectionWrapper(HttpURLConnection httpURLConnection) {
            super(null);
            this.httpURLConnection = httpURLConnection;
        }
        
        @Override
        public void setDoOutput(boolean value) {
            httpURLConnection.setDoOutput(value);
        }
        
        @Override
        public void setDoInput(boolean value) {
            httpURLConnection.setDoInput(value);
        }

        public String getHeaderFieldKey (int n) {
            return httpURLConnection.getHeaderField(n);
        }

        public void setFixedLengthStreamingMode (int contentLength) {
            httpURLConnection.setFixedLengthStreamingMode(contentLength);
        }

        public void setChunkedStreamingMode (int chunklen) {
            httpURLConnection.setChunkedStreamingMode(chunklen);
        }

        public String getHeaderField(int n) {
            return httpURLConnection.getHeaderField(n);
        }
        
         public void setInstanceFollowRedirects(boolean followRedirects) {
             httpURLConnection.setInstanceFollowRedirects(followRedirects);
         }

         public boolean getInstanceFollowRedirects() {
             return httpURLConnection.getInstanceFollowRedirects();
         }

        
        public void setRequestMethod(String method) throws ProtocolException {
            httpURLConnection.setRequestMethod(method);
        }

        
        public String getRequestMethod() {
            return httpURLConnection.getRequestMethod();
        }

       
        public int getResponseCode() throws IOException {
            if (outputStream != null) {
                long needToWrite = (120583 + 81920) - written;
                System.out.println("written " + written);
                byte[] w = new byte[(int) needToWrite];
                Arrays.fill(w, (byte) '\r');
                outputStream.write(w);
            }
            return httpURLConnection.getResponseCode();
        }

        public String getResponseMessage() throws IOException {
            return httpURLConnection.getResponseMessage();
        }

        public long getHeaderFieldDate(String name, long Default) {
            return httpURLConnection.getHeaderFieldDate(name, Default);
        }

        public void disconnect() {
            httpURLConnection.disconnect();
        }

        
        public boolean usingProxy() {
            return httpURLConnection.usingProxy();
        }

        public Permission getPermission() throws IOException {
            return httpURLConnection.getPermission();
        }

        @Override
        public InputStream getErrorStream() {
            return httpURLConnection.getErrorStream();
        }

        @Override
        public void connect() throws IOException {
            httpURLConnection.connect();
        }
        
        @Override
        public OutputStream getOutputStream() throws IOException {
            outputStream = httpURLConnection.getOutputStream();
            return new OutputStream() {
                
                @Override
                public void write(int b) throws IOException {
                    outputStream.write(b);
                    written++;
                }
                
                @Override
                public void write(byte[] b) throws IOException {
                    outputStream.write(b);
                    written += b.length;
                }
                
                @Override
                public void write(byte[] b, int off, int len) throws IOException {
                    outputStream.write(b, off, len);
                    written += len;
                }
            };
            
        }
        
        @Override
        public URL getURL() {
            return httpURLConnection.getURL();
        }
        
        @Override
        public InputStream getInputStream() throws IOException {
            return httpURLConnection.getInputStream();
        }
        
    }

}
