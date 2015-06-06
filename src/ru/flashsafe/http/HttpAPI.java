/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.flashsafe.http;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import ru.flashsafe.model.FSData;
import ru.flashsafe.model.FSMeta;
import ru.flashsafe.model.FSObject;

/**
 * Something like REST-client
 * @author alex_xpert
 */
public class HttpAPI {
    private static final String API_URL = "https://flashsafe-alpha.azurewebsites.net";
    private static final ThreadLocal<JsonParser> THREAD_CACHE = new ThreadLocal<>();
    private static final Gson GSON = new Gson();
    private static final Properties PROPERTIES = new Properties();
    // Эти поля будут использоваться всеми потоками и, при необходимости, обновляться повторной авторизацией.
    // Дабы несколько потоков не инициировали авторизацию одновременно, метод auth() синхронизирован.
    private static volatile String token;
    private static volatile long timeout;
    
    public static JsonObject get(String script, String request) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(API_URL + script + "?" + request).openConnection();
            connection.setDoOutput(true);
            connection.connect();
            OutputStream out = connection.getOutputStream();
            out.write(("access_token=" + token).getBytes());
            out.flush();
            out.close();
            InputStream in = connection.getInputStream();
            JsonElement response = getParser().parse(new JsonReader(new InputStreamReader(in)));
            in.close();
            connection.disconnect();
            return response.getAsJsonObject();
        } catch(IOException ioe) {
            return null;
        }
    }
    
    public static JsonObject post(String script, String request) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(API_URL + script).openConnection();
            connection.setDoOutput(true);
            connection.connect();
            OutputStream out = connection.getOutputStream();
            out.write(request.getBytes());
            out.flush();
            out.close();
            InputStream in = connection.getInputStream();
            StringBuilder sb = new StringBuilder();
            int b;
            while((b = in.read()) != -1) sb.append((char) b);
            in.close();
            connection.disconnect();
            System.out.println(sb);
            JsonElement response = getParser().parse(sb.toString());
            return response.getAsJsonObject();
        } catch(IOException ioe) {
            return null;
        }
    }
    
    public static synchronized int auth() {
        if (timeout - System.currentTimeMillis() > 0L) {
            return 1;
        }
        readProperties();
        // Step 1
        JsonObject result = post("/auth.php", "id=" + PROPERTIES.getProperty("id"));
        if(result != null) {
            // Step 2
            FSData data = (FSData)GSON.fromJson(result.get("data"), FSData.class);
            String hash = md5(data.token + PROPERTIES.getProperty("secret") + data.timestamp);
            result = post("/auth.php", "id=" + PROPERTIES.getProperty("id") + "&access_token=" + hash);
            if(result != null) {
                data = (FSData)GSON.fromJson(result.get("data"), FSData.class);
                token = data.token;
                timeout = System.currentTimeMillis() + data.timeout * 1000;
                return 1;
            }
        }
        return 0;
    }
    
    public static FSObject[] getContent() {
        return getContent(0);
    }
   
    public static FSObject[] getContent(int id) {
        if (timeout - System.currentTimeMillis() <= 0) {
            auth();
        }
        JsonObject result = get("/dir.php", "id=" + id);
        FSMeta meta = GSON.fromJson(result.get("meta"), FSMeta.class);
        if (meta.code == 200 && meta.msg.equals("ok")) {
            JsonArray data = result.getAsJsonArray("data");
            FSObject[] content = new FSObject[data.size()];
            for (int i=0;i<data.size();i++) {
                content[i] = GSON.fromJson(data.get(i), FSObject.class);
            }
            return content;
        }
        return null;
    }
    
    public static String md5(final String s) {
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
            return null;
        }
    }
    
    private static void readProperties(){
        try {
            PROPERTIES.loadFromXML(new FileInputStream("./flashsafe.xml"));
        } catch (Exception e) {
            PROPERTIES.setProperty("id", "1");
            PROPERTIES.setProperty("secret", "open123458");
        }
    }
   
    private static JsonParser getParser() {
        JsonParser parser = THREAD_CACHE.get();
        if (parser == null) {
            parser = new JsonParser();
            THREAD_CACHE.set(parser);
        }
        return parser;
    }
    
}
