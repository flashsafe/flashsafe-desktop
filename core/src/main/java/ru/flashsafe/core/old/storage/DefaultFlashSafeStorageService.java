package ru.flashsafe.core.old.storage;

import java.nio.file.Path;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

import ru.flashsafe.core.old.storage.rest.ContentTypeFixerFilter;
import ru.flashsafe.core.old.storage.rest.FlashSafeAuthFeature;
import ru.flashsafe.core.old.storage.rest.data.CreateDirectoryResponse;
import ru.flashsafe.core.old.storage.rest.data.CreateDirectoryResponse.CreateDirectoryResponseMeta;
import ru.flashsafe.core.old.storage.rest.data.DirectoryListResponse;
import ru.flashsafe.core.old.storage.rest.data.ResponseMeta;
import ru.flashsafe.core.storage.FlashSafeStorageService;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class DefaultFlashSafeStorageService implements FlashSafeStorageService {

    /* "http://localhost:8088/hackathon-webapi/api/reviewee-request/" */
    private static final String STORAGE_API_URL = "https://flashsafe-alpha.azurewebsites.net";

    private static final String DIRECTORY_API_PATH = "dir.php";

    private static final String DIRECTORY_ID_PARAMETER = "dir_id";
    
    private final Client restClient;
    
    private final WebTarget directoryTarget;
    
    public DefaultFlashSafeStorageService() {
        restClient = ClientBuilder.newBuilder().register(JacksonFeature.class).register(FlashSafeAuthFeature.class)
                .register(ContentTypeFixerFilter.class).register(MultiPartFeature.class).build();
        directoryTarget = restClient.target(STORAGE_API_URL).path(DIRECTORY_API_PATH);
    }

    @Override
    public List<FlashSafeStorageFileObject> list(long directoryId) {
        Response response = directoryTarget.queryParam(DIRECTORY_ID_PARAMETER, directoryId)
                .request(MediaType.APPLICATION_JSON_TYPE).get();
        DirectoryListResponse listResponse = response.readEntity(DirectoryListResponse.class);
        ResponseMeta responseMeta = listResponse.getResponseMeta();
        if (responseMeta.getResponseCode() == 200) {
            return listResponse.getFileObjects();
        }
        return null;
    }

    @Override
    public FlashSafeStorageDirectory createDirectory(long parentDirectoryId, String name) {
        Response response = directoryTarget.queryParam(DIRECTORY_ID_PARAMETER, parentDirectoryId)
                .request(MediaType.APPLICATION_JSON_TYPE).post(Entity.form(new Form("create", name)));
        CreateDirectoryResponse createDirectoryRespose = response.readEntity(CreateDirectoryResponse.class);
        CreateDirectoryResponseMeta meta = createDirectoryRespose.getResponseMeta();
        if (meta.getResponseCode() == 200) {
            return createLocalRepresentation(meta.getDirectoryId(), name);
        }
        return null;
    }

    // TODO get directory attributes from back-end
    private static FlashSafeStorageDirectory createLocalRepresentation(long directoryId, String name) {
        FlashSafeStorageDirectory directory = new FlashSafeStorageDirectory();
        return directory;
    }

    @Override
    public void downloadFile(long fileId, Path directory) {
        // TODO Auto-generated method stub
        throw new NotImplementedException();
    }

    @Override
    public void uploadFile(long directoryId, Path file) {
        MultiPart multiPart = new MultiPart();
        multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);

        FileDataBodyPart fileDataBodyPart = new FileDataBodyPart("file", file.toFile());
        multiPart.bodyPart(fileDataBodyPart);

        Response 
            response = directoryTarget.request(MediaType.APPLICATION_JSON_TYPE).post(
                    Entity.entity(multiPart, multiPart.getMediaType()));
            System.out.println(response.readEntity(String.class));
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

    public static void main(String[] args) {
        DefaultFlashSafeStorageService service = new DefaultFlashSafeStorageService();
        
        // service.createDirectory(0, "Alf folder");
         /*service.uploadFile(0,
         Paths.get("D:/Перепродажа через одностраничные сайты.docx"));*/
         List<FlashSafeStorageFileObject> secondList = service.list(0);
         List<FlashSafeStorageFileObject> secondList2 = service.list(0);
        System.out.println(4);
    }

}
