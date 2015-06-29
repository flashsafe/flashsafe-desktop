package ru.flashsafe.core.storage;

import java.nio.file.Path;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class DefaultFlashSafeStorageService implements FlashSafeStorageService {

    private static final String API_URL = "https://flashsafe-alpha.azurewebsites.net";
    
    @Override
    public FlashSafeStorageFileObject list(long directoryId) {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://localhost:8088/hackathon-webapi/api/reviewee-request/");
        WebTarget resourceWebTarget = webTarget.path("4");
        Response response = resourceWebTarget.queryParam("dir_id", 0).queryParam("access_token", 0).request(MediaType.APPLICATION_JSON_TYPE).get();
        DirectoryListResponse i = response.readEntity(DirectoryListResponse.class);
        System.out.println(i);
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public FlashSafeStorageDirectory createDirectory(long parentDirectoryId, String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void downloadFile(long fileId, Path directory) {
        // TODO Auto-generated method stub

    }

    @Override
    public void uploadFile(long directoryId, Path file) {
        // TODO Auto-generated method stub

    }

    @Override
    public void copy(long fileObjectId, long destinationDirectoryId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void move(long fileObjectId, long destinationDirectoryId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void delete(long fileObjectId) {
        // TODO Auto-generated method stub

    }
    
    public static void main(String[] args) {
        DefaultFlashSafeStorageService service = new DefaultFlashSafeStorageService();
        service.list(0);
    }

}
