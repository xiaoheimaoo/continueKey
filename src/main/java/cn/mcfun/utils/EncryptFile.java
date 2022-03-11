package cn.mcfun.utils;

import cn.mcfun.entity.UserInfo;
import cn.mcfun.request.GetRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.http.impl.client.BasicCookieStore;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.CRC32;

public class EncryptFile {
    public static String appVer = "";
    public static String assetbundleFolder = "";
    public static String dataServerFolderCrc = "";
    public static String dataVer = "";
    public static String dateVer = "";
    public static String animalName = "";
    public static byte[] key;
    public static byte[] iv;

    public String getFile(String event) {
        String result;
        JSONObject eventJson = JSONObject.parseObject(event);
        if (eventJson.getString("httpMethod").equals("GET")) {
            if (eventJson.getJSONObject("queryString").getString("key") != null && eventJson.getJSONObject("queryString").getString("pwd") != null && eventJson.getJSONObject("queryString").getString("key").length() == 10 && eventJson.getJSONObject("queryString").getString("pwd").length() >= 4 &&
                    eventJson.getJSONObject("queryString").getString("userId") == null && eventJson.getJSONObject("queryString").getString("authKey") == null && eventJson.getJSONObject("queryString").getString("secretKey") == null && eventJson.getJSONObject("queryString").getString("encryptFile") == null) {
                UserInfo userInfo = new UserInfo();
                userInfo.setCookie(new BasicCookieStore());
                String get = new GetRequest().sendGet(userInfo, "https://raw.githubusercontent.com/xiaoheimaoo/FGOData/master/mstVer.json");
                JSONObject res = JSONObject.parseObject(get);
                appVer = res.getString("appVer");
                dataVer = res.getString("dataVer");
                dateVer = res.getString("dateVer");
                assetbundleFolder = res.getString("folderName");
                CRC32 crc32 = new CRC32();
                crc32.update(assetbundleFolder.getBytes(StandardCharsets.UTF_8));
                dataServerFolderCrc = String.valueOf(crc32.getValue());
                animalName = res.getString("animalName");
                byte[] a = EncryptFile.animalName.getBytes();
                key = new byte[32];
                for (int i = 0; i < 32; i++) {
                    key[i] = (byte) (a[i] ^ 4);
                }
                iv = new byte[a.length - 32];
                for (int i = 0; i < a.length - 32; i++) {
                    iv[i] = (byte) (a[i + 32] ^ 8);
                }
                userInfo.setKey(eventJson.getJSONObject("queryString").getString("key"));
                userInfo.setPass(eventJson.getJSONObject("queryString").getString("pwd"));
                result = new ContinueKeyLogin().regist(userInfo);
            } else if (eventJson.getJSONObject("queryString").getString("userId") != null && eventJson.getJSONObject("queryString").getString("authKey") != null && eventJson.getJSONObject("queryString").getString("secretKey") != null && eventJson.getJSONObject("queryString").getString("userId").length() >= 7 &&
                    eventJson.getJSONObject("queryString").getString("key") == null && eventJson.getJSONObject("queryString").getString("pwd") == null && eventJson.getJSONObject("queryString").getString("encryptFile") == null) {
                UserInfo userInfo = new UserInfo();
                userInfo.setUserId(eventJson.getJSONObject("queryString").getString("userId"));
                userInfo.setAuthKey(eventJson.getJSONObject("queryString").getString("authKey"));
                userInfo.setSecretKey(eventJson.getJSONObject("queryString").getString("secretKey"));
                userInfo.setCookie(new BasicCookieStore());
                String get = new GetRequest().sendGet(userInfo, "https://raw.githubusercontent.com/xiaoheimaoo/FGOData/master/mstVer.json");
                JSONObject res = JSONObject.parseObject(get);
                appVer = res.getString("appVer");
                dataVer = res.getString("dataVer");
                dateVer = res.getString("dateVer");
                assetbundleFolder = res.getString("folderName");
                CRC32 crc32 = new CRC32();
                crc32.update(assetbundleFolder.getBytes(StandardCharsets.UTF_8));
                dataServerFolderCrc = String.valueOf(crc32.getValue());
                animalName = res.getString("animalName");
                byte[] a = EncryptFile.animalName.getBytes();
                key = new byte[32];
                for (int i = 0; i < 32; i++) {
                    key[i] = (byte) (a[i] ^ 4);
                }
                iv = new byte[a.length - 32];
                for (int i = 0; i < a.length - 32; i++) {
                    iv[i] = (byte) (a[i + 32] ^ 8);
                }
                result = new EncryptFileLogin().topLogin(userInfo);
            } else if (eventJson.getJSONObject("queryString").getString("encryptFile") != null &&
                    eventJson.getJSONObject("queryString").getString("key") == null && eventJson.getJSONObject("queryString").getString("pwd") == null && eventJson.getJSONObject("queryString").getString("userId") == null && eventJson.getJSONObject("queryString").getString("authKey") == null && eventJson.getJSONObject("queryString").getString("secretKey") == null) {
                try {
                    String encryptFile = new TripleDES().decryptMode(eventJson.getJSONObject("queryString").getString("encryptFile"));
                    JSONObject encryptFileJson = JSONObject.parseObject(encryptFile);
                    UserInfo userInfo = new UserInfo();
                    userInfo.setUserId(encryptFileJson.getString("userId"));
                    userInfo.setAuthKey(encryptFileJson.getString("authKey"));
                    userInfo.setSecretKey(encryptFileJson.getString("secretKey"));
                    userInfo.setCookie(new BasicCookieStore());
                    String get = new GetRequest().sendGet(userInfo, "https://raw.githubusercontent.com/xiaoheimaoo/FGOData/master/mstVer.json");
                    JSONObject res = JSONObject.parseObject(get);
                    appVer = res.getString("appVer");
                    dataVer = res.getString("dataVer");
                    dateVer = res.getString("dateVer");
                    assetbundleFolder = res.getString("folderName");
                    CRC32 crc32 = new CRC32();
                    crc32.update(assetbundleFolder.getBytes(StandardCharsets.UTF_8));
                    dataServerFolderCrc = String.valueOf(crc32.getValue());
                    animalName = res.getString("animalName");
                    byte[] a = EncryptFile.animalName.getBytes();
                    key = new byte[32];
                    for (int i = 0; i < 32; i++) {
                        key[i] = (byte) (a[i] ^ 4);
                    }
                    iv = new byte[a.length - 32];
                    for (int i = 0; i < a.length - 32; i++) {
                        iv[i] = (byte) (a[i + 32] ^ 8);
                    }
                    result = new EncryptFileLogin().topLogin(userInfo);
                } catch (Exception e) {
                    result = "存档码解析错误！";
                }
            } else {
                result = "请求参数错误！";
            }
        } else if (eventJson.getString("httpMethod").equals("POST")) {
            String body = eventJson.getString("body");
            JSONObject obj = new JSONObject();
            try {
                obj = JSONObject.parseObject(body);
            } catch (Exception e) {
                String[] params = body.split("&");
                for (int i = 0; i < params.length; i++) {
                    String[] param = params[i].split("=");
                    if (param.length >= 2) {
                        String key = param[0];
                        String value = param[1];
                        for (int j = 2; j < param.length; j++) {
                            value += "=" + param[j];
                        }
                        try {
                            obj.put(key, value);
                        } catch (JSONException e2) {
                            e2.printStackTrace();
                        }
                    }
                }
            }
            if (obj.getString("key") != null && obj.getString("pwd") != null && obj.getString("key").length() == 10 && obj.getString("pwd").length() >= 4 &&
                    obj.getString("userId") == null && obj.getString("authKey") == null && obj.getString("secretKey") == null && obj.getString("encryptFile") == null) {
                UserInfo userInfo = new UserInfo();
                userInfo.setCookie(new BasicCookieStore());
                String get = new GetRequest().sendGet(userInfo, "https://raw.githubusercontent.com/xiaoheimaoo/FGOData/master/mstVer.json");
                JSONObject res = JSONObject.parseObject(get);
                appVer = res.getString("appVer");
                dataVer = res.getString("dataVer");
                dateVer = res.getString("dateVer");
                assetbundleFolder = res.getString("folderName");
                CRC32 crc32 = new CRC32();
                crc32.update(assetbundleFolder.getBytes(StandardCharsets.UTF_8));
                dataServerFolderCrc = String.valueOf(crc32.getValue());
                animalName = res.getString("animalName");
                byte[] a = EncryptFile.animalName.getBytes();
                key = new byte[32];
                for (int i = 0; i < 32; i++) {
                    key[i] = (byte) (a[i] ^ 4);
                }
                iv = new byte[a.length - 32];
                for (int i = 0; i < a.length - 32; i++) {
                    iv[i] = (byte) (a[i + 32] ^ 8);
                }
                userInfo.setKey(obj.getString("key"));
                userInfo.setPass(obj.getString("pwd"));
                result = new ContinueKeyLogin().regist(userInfo);
            } else if (obj.getString("userId") != null && obj.getString("authKey") != null && obj.getString("secretKey") != null && obj.getString("userId").length() >= 7 &&
                    obj.getString("key") == null && obj.getString("pwd") == null && obj.getString("encryptFile") == null) {
                UserInfo userInfo = new UserInfo();
                userInfo.setUserId(obj.getString("userId"));
                userInfo.setAuthKey(obj.getString("authKey"));
                userInfo.setSecretKey(obj.getString("secretKey"));
                userInfo.setCookie(new BasicCookieStore());
                String get = new GetRequest().sendGet(userInfo, "https://raw.githubusercontent.com/xiaoheimaoo/FGOData/master/mstVer.json");
                JSONObject res = JSONObject.parseObject(get);
                appVer = res.getString("appVer");
                dataVer = res.getString("dataVer");
                dateVer = res.getString("dateVer");
                assetbundleFolder = res.getString("folderName");
                CRC32 crc32 = new CRC32();
                crc32.update(assetbundleFolder.getBytes(StandardCharsets.UTF_8));
                dataServerFolderCrc = String.valueOf(crc32.getValue());
                animalName = res.getString("animalName");
                byte[] a = EncryptFile.animalName.getBytes();
                key = new byte[32];
                for (int i = 0; i < 32; i++) {
                    key[i] = (byte) (a[i] ^ 4);
                }
                iv = new byte[a.length - 32];
                for (int i = 0; i < a.length - 32; i++) {
                    iv[i] = (byte) (a[i + 32] ^ 8);
                }
                result = new EncryptFileLogin().topLogin(userInfo);
            } else if (obj.getString("encryptFile") != null &&
                    obj.getString("key") == null && obj.getString("pwd") == null && obj.getString("userId") == null && obj.getString("authKey") == null && obj.getString("secretKey") == null) {
                try {
                    String encryptFile = new TripleDES().decryptMode(obj.getString("encryptFile"));
                    JSONObject encryptFileJson = JSONObject.parseObject(encryptFile);
                    UserInfo userInfo = new UserInfo();
                    userInfo.setUserId(encryptFileJson.getString("userId"));
                    userInfo.setAuthKey(encryptFileJson.getString("authKey"));
                    userInfo.setSecretKey(encryptFileJson.getString("secretKey"));
                    userInfo.setCookie(new BasicCookieStore());
                    String get = new GetRequest().sendGet(userInfo, "https://raw.githubusercontent.com/xiaoheimaoo/FGOData/master/mstVer.json");
                    JSONObject res = JSONObject.parseObject(get);
                    appVer = res.getString("appVer");
                    dataVer = res.getString("dataVer");
                    dateVer = res.getString("dateVer");
                    assetbundleFolder = res.getString("folderName");
                    CRC32 crc32 = new CRC32();
                    crc32.update(assetbundleFolder.getBytes(StandardCharsets.UTF_8));
                    dataServerFolderCrc = String.valueOf(crc32.getValue());
                    animalName = res.getString("animalName");
                    byte[] a = EncryptFile.animalName.getBytes();
                    key = new byte[32];
                    for (int i = 0; i < 32; i++) {
                        key[i] = (byte) (a[i] ^ 4);
                    }
                    iv = new byte[a.length - 32];
                    for (int i = 0; i < a.length - 32; i++) {
                        iv[i] = (byte) (a[i + 32] ^ 8);
                    }
                    result = new EncryptFileLogin().topLogin(userInfo);
                } catch (Exception e) {
                    result = "存档码解析错误！";
                }
            } else {
                result = "请求参数错误！";
            }
        } else {
            result = "不支持的请求方式！";
        }
        JSONObject json = new JSONObject();
        json.put("isBase64Encoded", false);
        json.put("statusCode", 200);
        json.put("headers", JSONObject.parseObject("{\"Content-Type\":\"application/json;charset=utf-8\"}"));
        try {
            json.put("body", JSON.toJSONString(JSONObject.parseObject(result), SerializerFeature.PrettyFormat));
        } catch (Exception e) {

        } finally {
            json.put("body", result);
        }
        System.out.println("--------------------记录数据3--------------------");
        System.out.println(result);
        System.out.println("------------------------------------------------------");
        return json.toJSONString();
    }
}