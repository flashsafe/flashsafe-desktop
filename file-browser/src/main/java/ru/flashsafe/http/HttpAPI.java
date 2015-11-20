package ru.flashsafe.http;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//import ru.flashsafe.controller.MainSceneController;
import ru.flashsafe.model.FSData;
import ru.flashsafe.model.FSMeta;
import ru.flashsafe.model.FSObject;
//import ru.flashsafe.token.FlashSafeToken;
//import ru.flashsafe.token.exception.CodeGenerationException;
//import ru.flashsafe.token.exception.FlashSafeTokenNotFoundException;
//import ru.flashsafe.token.exception.FlashSafeTokenUnavailableException;

/**
 * Something like REST-client
 * @author alex_xpert
 */
public class HttpAPI {
    //private static final Logger log = LogManager.getLogger(HttpAPI.class);
    private static final String API_URL = "https://flashsafe-alpha.azurewebsites.net";
    private static final ThreadLocal<JsonParser> THREAD_CACHE = new ThreadLocal<>();
    private static final Gson GSON = new Gson();
    //@Deprecated
    private static final Properties PROPERTIES = new Properties();
    
    // Эти поля будут использоваться всеми потоками и, при необходимости, обновляться повторной авторизацией.
    // Дабы несколько потоков не инициировали авторизацию одновременно, метод auth() синхронизирован.
    private static volatile String token;
    private static volatile long timeout;
    
    private static final HashMap<String, String> MIME = new HashMap();
    private static final ArrayList<UploadProgressListener> LISTENERS = new ArrayList();
    
    private static HttpAPI API;
    
    private HttpAPI() {}
    
    public static final HttpAPI getInstance() {
        if(API == null) {
            API = new HttpAPI();
            loadMimeTypes();
        }
        return API;
    }
    
    public void addListener(UploadProgressListener listener) {
        LISTENERS.add(listener);
    }
    
    public void removeListener(UploadProgressListener listener) {
        LISTENERS.remove(listener);
    }
    
    private JsonObject get(String script, String request) {
        HttpURLConnection connection = null;
        InputStream in = null;
        try {
            URL url = new URL(API_URL + script + "?access_token=" + token + "&" + request);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            in = connection.getInputStream();
            JsonElement response = getParser().parse(new InputStreamReader(in));
            in.close();
            connection.disconnect();
            return response.getAsJsonObject();
        } catch(IOException ioe) {
            //log.error("Error on GET request", ioe);
            return null;
        } finally {
            try {
                if(connection != null) {
                    connection.disconnect();
                }
                if(in != null) {
                    in.close();
                }
            } catch(IOException ioe) {
                //log.error("Error on closing HttpURLConnection and InputStream", ioe);
            }
        }
    }
    
    private JsonObject post(String script, HashMap<String, String> request, File file) {
        HttpURLConnection connection = null;
        InputStream in = null;
        OutputStream out = null;
        try {
            if(token != null) {
                request.put("access_token", token);
            }
            connection = (HttpURLConnection) new URL(API_URL + script).openConnection();
            connection.setDoOutput(true);
            String boundary = generateBoundary();
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            String start = "";
            for(String name : request.keySet()) {
                start += "--" + boundary + "\r\n"
                        + "Content-Disposition: form-data; name=\"" + name + "\"\r\n\r\n"
                        + request.get(name) + "\r\n";
            }
            if(file != null) {
                start += "--" + boundary + "\r\n"
                        + "Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"\r\n"
                        + "Content-Type: "
                        + MIME.getOrDefault(file.getName().split("\\.")[1], "application/octet-stream") + "\r\n\r\n";
            }
            String end = "--" + boundary + "--\r\n";
            int length = start.length() + (file == null ? 0 : 2 + (int) file.length()) + end.length();
            connection.setRequestProperty("Content-Length", "" + length);
            connection.connect();
            out = connection.getOutputStream();
            out.write(start.getBytes("UTF-8"));
            if(file != null) {
                FileInputStream fis = new FileInputStream(file);
                double total = file.length();
                double uploaded = 0;
                byte[] block = new byte[1024];
                int b;
                while((b = fis.read(block)) != -1) {
                    out.write(block);
                    uploaded += b;
                    for(UploadProgressListener upl : LISTENERS) {
                        upl.onUpdateProgress(uploaded / total);
                    }
                }
                fis.close();
                out.write("\r\n".getBytes("UTF-8"));
            }
            out.write(end.getBytes("UTF-8"));
            out.flush();
            out.close();
            in = connection.getInputStream();
            JsonElement response = getParser().parse(new InputStreamReader(in));
            in.close();
            connection.disconnect();
            return response.getAsJsonObject();
        } catch(IOException ioe) {
            //log.error("Error on POST request", ioe);
            return null;
        } finally {
            try {
                if(connection != null) {
                    connection.disconnect();
                }
                if(in != null) {
                    in.close();
                }
                if(out != null) {
                    out.close();
                }
            } catch(IOException ioe) {
                //log.error("Error on closing HttpURLConnection, InputStream and OutputStream", ioe);
            }
        }
    }
    
    public synchronized boolean auth() {
        //try {
            if (timeout - System.currentTimeMillis() > 0L) {
                return true;
            }
            readProperties();
            //FlashSafeToken fstoken = MainSceneController.rets.lookup("1");
            // Step 1
            HashMap<String, String> form = new HashMap();
            form.put("id", PROPERTIES.getProperty("id")/*fstoken.getId()*/);
            JsonObject result = post("/auth.php", form, null);
            if(result != null) {
                // Step 2
                FSData data = (FSData)GSON.fromJson(result.get("data"), FSData.class);
                String hash = md5(data.token + PROPERTIES.getProperty("secret")/*fstoken.generateCode("")*/ + data.timestamp);
                form.put("access_token", hash);
                result = post("/auth.php", form, null);
                if(result != null) {
                    data = (FSData)GSON.fromJson(result.get("data"), FSData.class);
                    token = data.token;
                    timeout = System.currentTimeMillis() + data.timeout * 1000;
                    return true;
                }
            }
        //} catch(FlashSafeTokenNotFoundException | CodeGenerationException | FlashSafeTokenUnavailableException e) {
            //log.error(e);
        //}
        return false;
    }
    
    /**
     * Get content from directory
     * @return Array of the objects, when first is the content or null
     * and second is the result code:
     * 0 - OK
     * 1 - Directory is empty
     * 2 - Directory not found
     * 3 - Pincode is incorrect, need a valid pincode
     * 4 - Server error
     */
    public Object[] getContent() {
        return getContent(0, "");
    }
   
    /**
     * Get content from directory
     * @param id
     * @param pincode
     * @return Array of the objects, when first is the content or null
     * and second is the result code:
     * 0 - OK
     * 1 - Directory is empty
     * 2 - Directory not found
     * 3 - Pincode is incorrect, need a valid pincode
     * 4 - Server error
     */
    public Object[] getContent(int id, String pincode) {
        Object[] answer = new Object[2];
        if (timeout - System.currentTimeMillis() <= 0) {
            auth();
        }
        JsonObject result = get("/dir.php", "dir_id=" + String.valueOf(id) + (pincode.equals("") ? "" : "&pincode=" + pincode));
        FSMeta meta = GSON.fromJson(result.get("meta"), FSMeta.class);
        switch(meta.code) {
            case 200:
                if (meta.msg.equals("ok")) {
                    JsonArray data = result.getAsJsonArray("data");
                    FSObject[] content = new FSObject[data.size()];
                    for (int i=0;i<data.size();i++) {
                        content[i] = GSON.fromJson(data.get(i), FSObject.class);
                    }
                    answer[0] = content;
                    answer[1] = 0;
                } else {
                    answer[0] = null;
                    answer[1] = 1;
                }
            break;
            case 404:
                answer[0] = null;
                answer[1] = 2;
                break;
            case 423:
                if(meta.msg.equals("take_token")) {
                    auth();
                    getContent(id, pincode);
                } else {
                    answer[0] = null;
                    answer[1] = 3;
                }
            break;
            case 911:
                answer[0] = null;
                answer[1] = 4;
                break;
        }
        return answer;
    }
    
    public int createPath(int parent, String pincode, String name) {
        if (timeout - System.currentTimeMillis() <= 0) {
            auth();
        }
        JsonObject result = get("/dir.php", "dir_id=" + parent + (pincode.equals("") ? "" : "&pincode=" + pincode) + "&create=" + name);
        FSMeta meta = GSON.fromJson(result.get("meta"), FSMeta.class);
        if(meta.code == 200) {
            return meta.dir_id;
        } else if(meta.code == 423) {
            if(meta.msg.equals("take_token")) {
                auth();
                createPath(parent, pincode, name);
            }
        }
        return 0;
    }
    
    public int uploadFile(int dir_id, String pincode, int file_id, File file) {
        if (timeout - System.currentTimeMillis() <= 0) {
            auth();
        }
        HashMap<String, String> form = new HashMap();
        form.put("dir_id", String.valueOf(dir_id));
        if(!pincode.equals("")) {
           form.put("pincode", pincode); 
        }
        if(file_id > -1) {
            form.put("file_id", String.valueOf(file_id));
        }
        JsonObject result = post("/dir.php", form, file);
        FSMeta meta = GSON.fromJson(result.get("meta"), FSMeta.class);
        System.out.println(meta.msg);
        if(meta.code == 200) {
            return meta.file_id;
        } else if(meta.code == 423) {
            if(meta.msg.equals("take_token")) {
                auth();
                uploadFile(dir_id, pincode, file_id, file);
            }
        }
        return -1;
    }
    
    private String generateBoundary() {
        String boundary = "";
        Random r = new Random();
        for(int i=0;i<10;i++) {
            boundary += r.nextInt(10);
        }
        return boundary;
    }
    
    public String md5(final String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException nsae) {
            //log.error("Error on generate MD5", nsae);
            return null;
        }
    }
    
    //@Deprecated
    private void readProperties(){
        if(!PROPERTIES.isEmpty()) return;
        try {
            PROPERTIES.loadFromXML(new FileInputStream("./flashsafe.xml"));
        } catch (Exception e) {
            //log.error("Error on loading properties, setting default values.", e);
            PROPERTIES.setProperty("id", "1");
            PROPERTIES.setProperty("secret", "open123458");
        }
    }
   
    private JsonParser getParser() {
        JsonParser parser = THREAD_CACHE.get();
        if (parser == null) {
            parser = new JsonParser();
            THREAD_CACHE.set(parser);
        }
        return parser;
    }
    
    private static void loadMimeTypes() {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream("./.mime");
            StringBuilder sb = new StringBuilder();
            int b;
            while((b = fis.read()) != -1) {
                sb.append((char) b);
            }
            String[] types = sb.toString().split("\n");
            for(String s : types) {
                String[] exts = s.split("=")[0].split(",");
                String type = s.split("=")[1];
                for(String ext : exts) {
                    MIME.put(ext, type);
                }
            }
        } catch(IOException ioe) {
            //log.error("Error on loading MIME-types.", ioe);
        } finally {
            try {
                if(fis != null) {
                    fis.close();
                }
            } catch(IOException ioe) {
                //log.error("Error on closing MIME-types InputStream.", ioe);
            }
        }
    }
    
}
