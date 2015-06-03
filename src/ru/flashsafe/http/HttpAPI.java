/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.flashsafe.http;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author alex_xpert
 */
public class HttpAPI {
    private static final String API_URL = "https://flashsafe-alpha.azurewebsites.net";
    private static HttpURLConnection connection;
    private static OutputStream out;
    private static InputStream in;
    private static final JsonParser parser = new JsonParser();
    private static JsonElement response;
    
    public static JsonObject get(String script, String request) {
        try {
            connection = (HttpURLConnection) new URL(API_URL + script + "?" + request).openConnection();
            connection.setDoOutput(true);
            connection.connect();
            out = connection.getOutputStream();
            out.write("pincode=1".getBytes());
            out.flush();
            out.close();
            in = connection.getInputStream();
            response = parser.parse(new JsonReader(new InputStreamReader(in)));
            in.close();
            connection.disconnect();
            return response.getAsJsonObject();
        } catch(IOException ioe) {
            return null;
        }
    }
    
    public static JsonObject post(String script, String request) {
        try {
            connection = (HttpURLConnection) new URL(API_URL + script).openConnection();
            connection.setDoOutput(true);
            connection.connect();
            out = connection.getOutputStream();
            out.write(request.getBytes());
            out.flush();
            out.close();
            in = connection.getInputStream();
            StringBuilder sb = new StringBuilder();
            int b;
            while((b = in.read()) != -1) sb.append((char) b);
            in.close();
            connection.disconnect();
            System.out.println(sb);
            response = parser.parse(sb.toString());
            return response.getAsJsonObject();
        } catch(IOException ioe) {
            return null;
        }
    }
    
    public static void auth() {
        // Step 1
        JsonObject result = post("/auth.php", "id=1");
        if(result != null) {
            // Step 2
            String hash = md5(result.get("data").getAsJsonObject().get("token").getAsString()
                        + "open123458"
                        + result.get("data").getAsJsonObject().get("timestamp").getAsString());
            result = post("/auth.php", "id=1&access_token=" + hash);
            if(result.get("meta").getAsJsonObject().get("code").getAsInt() == 423) {
                //System.out.println("Fail token");
            } else {
                //System.out.println("Success!");
                result = get("/dir.php", "access_token=" + result.get("data").getAsJsonObject().get("token").getAsString() + "&dir_id=1");
                System.out.println(result);
            }
        } else {
            //System.out.println("Can't connect to host");
        }
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
    
}
