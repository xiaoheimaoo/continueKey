package cn.mcfun.utils;

import cn.mcfun.entity.UserInfo;
import cn.mcfun.request.GetRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.http.impl.client.BasicCookieStore;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;

import static cn.mcfun.utils.AES.decryptWithAesCBC;

public class EncryptFile {
    public static String userCreateServer = "game.fate-go.jp";
    private static String mouseInfoMsgPackKey = "W0Juh4cFJSYPkebJB9WpswNF51oa6Gm7";
    public static String filePath = "/mnt/jp/";
    public static String appVer = "";
    public static String assetbundleFolder = "";
    public static String dataServerFolderCrc = "";
    public static String dataVer = "";
    public static String dateVer = "";
    public static String animalName = "";
    public static byte[] key;
    public static byte[] iv;

    public String getFile(String event) {
        String result = "未知错误，请重试！";
        JSONObject eventJson = JSONObject.parseObject(event);
        if (eventJson.getString("httpMethod").equals("GET")) {
            if(eventJson.getJSONObject("queryString").getString("userCreateServer") != null){
                if(eventJson.getJSONObject("queryString").getString("userCreateServer").toLowerCase().equals("jp")){
                    userCreateServer = "game.fate-go.jp";
                    mouseInfoMsgPackKey = "W0Juh4cFJSYPkebJB9WpswNF51oa6Gm7";
                    filePath = "/mnt/jp/";
                    if (eventJson.getJSONObject("queryString").getString("key") != null && eventJson.getJSONObject("queryString").getString("pwd") != null && eventJson.getJSONObject("queryString").getString("key").length() == 10 && eventJson.getJSONObject("queryString").getString("pwd").length() >= 4 &&
                            eventJson.getJSONObject("queryString").getString("userId") == null && eventJson.getJSONObject("queryString").getString("authKey") == null && eventJson.getJSONObject("queryString").getString("secretKey") == null && eventJson.getJSONObject("queryString").getString("saveData") == null) {
                        UserInfo userInfo = new UserInfo();
                        if(eventJson.getJSONObject("queryString").getString("newpwd") != null){
                            userInfo.setNewPass(eventJson.getJSONObject("queryString").getString("newpwd"));
                        }
                        if(eventJson.getJSONObject("queryString").getString("token") != null){
                            userInfo.setToken(eventJson.getJSONObject("queryString").getString("token"));
                        }
                        userInfo.setCookie(new BasicCookieStore());
                        Random rand = new Random(); //创建一个随机产生数类Scanner
                        int port = rand.nextInt(2099 - 100 + 1) + 100;
                        userInfo.setPort("ip"+port);
                        String top = new GetRequest().sendGet(userInfo,"https://"+userCreateServer+"/gamedata/top?appVer=2.44.0");
                        JSONObject res = JSONObject.parseObject(top);
                        if (res.getJSONArray("response").getJSONObject(0).getJSONObject("fail").getString("action") != null) {
                            if (res.getJSONArray("response").getJSONObject(0).getJSONObject("fail").getString("action").equals("app_version_up")) {
                                String newVersion = res.getJSONArray("response").getJSONObject(0).getJSONObject("fail").getString("detail");
                                Pattern pattern = Pattern.compile(".*新ver.：(.*)、現.*");
                                Matcher matcher = pattern.matcher(newVersion);
                                if(matcher.find()){
                                    appVer = matcher.group(1);
                                    top = new GetRequest().sendGet(userInfo,"https://"+userCreateServer+"/gamedata/top?appVer="+matcher.group(1));
                                    res = JSONObject.parseObject(top);
                                    dataVer = res.getJSONArray("response").getJSONObject(0).getJSONObject("success").getString("dataVer");
                                    dateVer = res.getJSONArray("response").getJSONObject(0).getJSONObject("success").getString("dateVer");
                                    String assetbundle = res.getJSONArray("response").getJSONObject(0).getJSONObject("success").getString("assetbundle");
                                    Map<String,Object> map = mouseInfoMsgPack(Base64.getDecoder().decode(assetbundle));
                                    assetbundleFolder = (String) map.get("folderName");
                                    CRC32 crc32 = new CRC32();
                                    crc32.update(assetbundleFolder.getBytes(StandardCharsets.UTF_8));
                                    dataServerFolderCrc = String.valueOf(crc32.getValue());
                                    animalName = (String) map.get("animalName");
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
                                    if(userInfo.getToken() == null){
                                        result = "token参数不能为空，用户未登录！";
                                    }else {
                                        result = new ContinueKeyLogin().regist(userInfo);
                                    }
                                }
                            }else{
                                result = res.getJSONArray("response").getJSONObject(0).getJSONObject("fail").getString("detail");
                            }
                        }else{
                            result = res.getJSONArray("response").getJSONObject(0).getJSONObject("fail").getString("detail");
                        }
                    } else if (eventJson.getJSONObject("queryString").getString("userId") != null && eventJson.getJSONObject("queryString").getString("authKey") != null && eventJson.getJSONObject("queryString").getString("secretKey") != null && eventJson.getJSONObject("queryString").getString("userId").length() >= 7 &&
                            eventJson.getJSONObject("queryString").getString("key") == null && eventJson.getJSONObject("queryString").getString("pwd") == null && eventJson.getJSONObject("queryString").getString("saveData") == null) {
                        UserInfo userInfo = new UserInfo();
                        if(eventJson.getJSONObject("queryString").getString("newpwd") != null){
                            userInfo.setNewPass(eventJson.getJSONObject("queryString").getString("newpwd"));
                        }
                        if(eventJson.getJSONObject("queryString").getString("token") != null){
                            userInfo.setToken(eventJson.getJSONObject("queryString").getString("token"));
                        }
                        userInfo.setUserId(eventJson.getJSONObject("queryString").getString("userId"));
                        userInfo.setAuthKey(eventJson.getJSONObject("queryString").getString("authKey"));
                        userInfo.setSecretKey(eventJson.getJSONObject("queryString").getString("secretKey"));
                        userInfo.setCookie(new BasicCookieStore());
                        Random rand = new Random(); //创建一个随机产生数类Scanner
                        int port = rand.nextInt(2099 - 100 + 1) + 100;
                        userInfo.setPort("ip"+port);
                        String top = new GetRequest().sendGet(userInfo,"https://"+userCreateServer+"/gamedata/top?appVer=2.44.0");
                        JSONObject res = JSONObject.parseObject(top);
                        if (res.getJSONArray("response").getJSONObject(0).getJSONObject("fail").getString("action") != null) {
                            if (res.getJSONArray("response").getJSONObject(0).getJSONObject("fail").getString("action").equals("app_version_up")) {
                                String newVersion = res.getJSONArray("response").getJSONObject(0).getJSONObject("fail").getString("detail");
                                Pattern pattern = Pattern.compile(".*新ver.：(.*)、現.*");
                                Matcher matcher = pattern.matcher(newVersion);
                                if(matcher.find()){
                                    appVer = matcher.group(1);
                                    top = new GetRequest().sendGet(userInfo,"https://"+userCreateServer+"/gamedata/top?appVer="+matcher.group(1));
                                    res = JSONObject.parseObject(top);
                                    dataVer = res.getJSONArray("response").getJSONObject(0).getJSONObject("success").getString("dataVer");
                                    dateVer = res.getJSONArray("response").getJSONObject(0).getJSONObject("success").getString("dateVer");
                                    String assetbundle = res.getJSONArray("response").getJSONObject(0).getJSONObject("success").getString("assetbundle");
                                    Map<String,Object> map = mouseInfoMsgPack(Base64.getDecoder().decode(assetbundle));
                                    assetbundleFolder = (String) map.get("folderName");
                                    CRC32 crc32 = new CRC32();
                                    crc32.update(assetbundleFolder.getBytes(StandardCharsets.UTF_8));
                                    dataServerFolderCrc = String.valueOf(crc32.getValue());
                                    animalName = (String) map.get("animalName");
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
                                }
                            }else{
                                result = res.getJSONArray("response").getJSONObject(0).getJSONObject("fail").getString("detail");
                            }
                        }else{
                            result = res.getJSONArray("response").getJSONObject(0).getJSONObject("fail").getString("detail");
                        }
                    } else if (eventJson.getJSONObject("queryString").getString("saveData") != null &&
                            eventJson.getJSONObject("queryString").getString("key") == null && eventJson.getJSONObject("queryString").getString("pwd") == null && eventJson.getJSONObject("queryString").getString("userId") == null && eventJson.getJSONObject("queryString").getString("authKey") == null && eventJson.getJSONObject("queryString").getString("secretKey") == null) {
                        try {
                            String saveData = new TripleDES().decryptMode(eventJson.getJSONObject("queryString").getString("saveData"));
                            JSONObject saveDataJson = JSONObject.parseObject(saveData);
                            UserInfo userInfo = new UserInfo();
                            if(eventJson.getJSONObject("queryString").getString("newpwd") != null){
                                userInfo.setNewPass(eventJson.getJSONObject("queryString").getString("newpwd"));
                            }
                            if(eventJson.getJSONObject("queryString").getString("token") != null){
                                userInfo.setToken(eventJson.getJSONObject("queryString").getString("token"));
                            }
                            userInfo.setUserId(saveDataJson.getString("userId"));
                            userInfo.setAuthKey(saveDataJson.getString("authKey"));
                            userInfo.setSecretKey(saveDataJson.getString("secretKey"));
                            userInfo.setCookie(new BasicCookieStore());
                            Random rand = new Random(); //创建一个随机产生数类Scanner
                            int port = rand.nextInt(2099 - 100 + 1) + 100;
                            userInfo.setPort("ip"+port);
                            String top = new GetRequest().sendGet(userInfo,"https://"+userCreateServer+"/gamedata/top?appVer=2.44.0");
                            JSONObject res = JSONObject.parseObject(top);
                            if (res.getJSONArray("response").getJSONObject(0).getJSONObject("fail").getString("action") != null) {
                                if (res.getJSONArray("response").getJSONObject(0).getJSONObject("fail").getString("action").equals("app_version_up")) {
                                    String newVersion = res.getJSONArray("response").getJSONObject(0).getJSONObject("fail").getString("detail");
                                    Pattern pattern = Pattern.compile(".*新ver.：(.*)、現.*");
                                    Matcher matcher = pattern.matcher(newVersion);
                                    if(matcher.find()){
                                        appVer = matcher.group(1);
                                        top = new GetRequest().sendGet(userInfo,"https://"+userCreateServer+"/gamedata/top?appVer="+matcher.group(1));
                                        res = JSONObject.parseObject(top);
                                        dataVer = res.getJSONArray("response").getJSONObject(0).getJSONObject("success").getString("dataVer");
                                        dateVer = res.getJSONArray("response").getJSONObject(0).getJSONObject("success").getString("dateVer");
                                        String assetbundle = res.getJSONArray("response").getJSONObject(0).getJSONObject("success").getString("assetbundle");
                                        Map<String,Object> map = mouseInfoMsgPack(Base64.getDecoder().decode(assetbundle));
                                        assetbundleFolder = (String) map.get("folderName");
                                        CRC32 crc32 = new CRC32();
                                        crc32.update(assetbundleFolder.getBytes(StandardCharsets.UTF_8));
                                        dataServerFolderCrc = String.valueOf(crc32.getValue());
                                        animalName = (String) map.get("animalName");
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
                                    }
                                }else{
                                    result = res.getJSONArray("response").getJSONObject(0).getJSONObject("fail").getString("detail");
                                }
                            }else{
                                result = res.getJSONArray("response").getJSONObject(0).getJSONObject("fail").getString("detail");
                            }
                        } catch (Exception e) {
                            result = "存档码解析错误！";
                        }
                    } else {
                        result = "请求参数错误！";
                    }
                }else if(eventJson.getJSONObject("queryString").getString("userCreateServer").toLowerCase().equals("us")){
                    userCreateServer = "game.fate-go.us";
                    mouseInfoMsgPackKey = "nn33CYId2J1ggv0bYDMbYuZ60m4GZt5P";
                    filePath = "/mnt/us/";
                    if (eventJson.getJSONObject("queryString").getString("key") != null && eventJson.getJSONObject("queryString").getString("pwd") != null && eventJson.getJSONObject("queryString").getString("key").length() == 10 && eventJson.getJSONObject("queryString").getString("pwd").length() >= 4 &&
                            eventJson.getJSONObject("queryString").getString("userId") == null && eventJson.getJSONObject("queryString").getString("authKey") == null && eventJson.getJSONObject("queryString").getString("secretKey") == null && eventJson.getJSONObject("queryString").getString("saveData") == null) {
                        UserInfo userInfo = new UserInfo();
                        if(eventJson.getJSONObject("queryString").getString("newpwd") != null){
                            userInfo.setNewPass(eventJson.getJSONObject("queryString").getString("newpwd"));
                        }
                        if(eventJson.getJSONObject("queryString").getString("token") != null){
                            userInfo.setToken(eventJson.getJSONObject("queryString").getString("token"));
                        }
                        userInfo.setCookie(new BasicCookieStore());
                        Random rand = new Random(); //创建一个随机产生数类Scanner
                        int port = rand.nextInt(2099 - 100 + 1) + 100;
                        userInfo.setPort("ip"+port);
                        long nowTime = System.currentTimeMillis() / 1000L;
                        String itunes = new GetRequest().sendGet(userInfo,"https://itunes.apple.com/us/lookup?bundleId=com.aniplex.fategrandorder.en&time="+nowTime);
                        JSONObject res = JSONObject.parseObject(itunes);
                        appVer = res.getJSONArray("results").getJSONObject(0).getString("version");
                        itunes = new GetRequest().sendGet2(userInfo,"https://game.fate-go.us/gamedata/top?appVer="+appVer);
                        res = JSONObject.parseObject(itunes);
                        if (res.getJSONArray("response").getJSONObject(0).getJSONObject("fail").containsKey("action") && res.getJSONArray("response").getJSONObject(0).getJSONObject("fail").getString("action").equals("maint")) {
                            result = res.getJSONArray("response").getJSONObject(0).getJSONObject("fail").getString("detail");
                        }else{
                            dataVer = res.getJSONArray("response").getJSONObject(0).getJSONObject("success").getString("dataVer");
                            dateVer = res.getJSONArray("response").getJSONObject(0).getJSONObject("success").getString("dateVer");
                            String assetbundle = res.getJSONArray("response").getJSONObject(0).getJSONObject("success").getString("assetbundle");
                            Map<String,Object> map = mouseInfoMsgPack(Base64.getDecoder().decode(assetbundle));
                            assetbundleFolder = (String) map.get("folderName");
                            CRC32 crc32 = new CRC32();
                            crc32.update(assetbundleFolder.getBytes(StandardCharsets.UTF_8));
                            dataServerFolderCrc = String.valueOf(crc32.getValue());
                            animalName = (String) map.get("animalName");
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
                            if(userInfo.getToken() == null){
                                result = "token参数不能为空，用户未登录！";
                            }else {
                                result = new ContinueKeyLogin().regist(userInfo);
                            }
                        }
                    } else if (eventJson.getJSONObject("queryString").getString("userId") != null && eventJson.getJSONObject("queryString").getString("authKey") != null && eventJson.getJSONObject("queryString").getString("secretKey") != null && eventJson.getJSONObject("queryString").getString("userId").length() >= 7 &&
                            eventJson.getJSONObject("queryString").getString("key") == null && eventJson.getJSONObject("queryString").getString("pwd") == null && eventJson.getJSONObject("queryString").getString("saveData") == null) {
                        UserInfo userInfo = new UserInfo();
                        if(eventJson.getJSONObject("queryString").getString("newpwd") != null){
                            userInfo.setNewPass(eventJson.getJSONObject("queryString").getString("newpwd"));
                        }
                        if(eventJson.getJSONObject("queryString").getString("token") != null){
                            userInfo.setToken(eventJson.getJSONObject("queryString").getString("token"));
                        }
                        userInfo.setUserId(eventJson.getJSONObject("queryString").getString("userId"));
                        userInfo.setAuthKey(eventJson.getJSONObject("queryString").getString("authKey"));
                        userInfo.setSecretKey(eventJson.getJSONObject("queryString").getString("secretKey"));
                        userInfo.setCookie(new BasicCookieStore());
                        Random rand = new Random(); //创建一个随机产生数类Scanner
                        int port = rand.nextInt(2099 - 100 + 1) + 100;
                        userInfo.setPort("ip"+port);
                        long nowTime = System.currentTimeMillis() / 1000L;
                        String itunes = new GetRequest().sendGet(userInfo,"https://itunes.apple.com/us/lookup?bundleId=com.aniplex.fategrandorder.en&time="+nowTime);
                        JSONObject res = JSONObject.parseObject(itunes);
                        appVer = res.getJSONArray("results").getJSONObject(0).getString("version");
                        itunes = new GetRequest().sendGet2(userInfo,"https://game.fate-go.us/gamedata/top?appVer="+appVer);
                        res = JSONObject.parseObject(itunes);
                        if (res.getJSONArray("response").getJSONObject(0).getJSONObject("fail").containsKey("action") && res.getJSONArray("response").getJSONObject(0).getJSONObject("fail").getString("action").equals("maint")) {
                            result = res.getJSONArray("response").getJSONObject(0).getJSONObject("fail").getString("detail");
                        }else{
                            dataVer = res.getJSONArray("response").getJSONObject(0).getJSONObject("success").getString("dataVer");
                            dateVer = res.getJSONArray("response").getJSONObject(0).getJSONObject("success").getString("dateVer");
                            String assetbundle = res.getJSONArray("response").getJSONObject(0).getJSONObject("success").getString("assetbundle");
                            Map<String,Object> map = mouseInfoMsgPack(Base64.getDecoder().decode(assetbundle));
                            assetbundleFolder = (String) map.get("folderName");
                            CRC32 crc32 = new CRC32();
                            crc32.update(assetbundleFolder.getBytes(StandardCharsets.UTF_8));
                            dataServerFolderCrc = String.valueOf(crc32.getValue());
                            animalName = (String) map.get("animalName");
                            byte[] a = EncryptFile.animalName.getBytes();
                            key = new byte[32];
                            for (int i = 0; i < 32; i++) {
                                key[i] = (byte) (a[i] ^ 4);
                            }
                            iv = new byte[a.length - 32];
                            for (int i = 0; i < a.length - 32; i++) {
                                iv[i] = (byte) (a[i + 32] ^ 8);
                            }
                            if(userInfo.getToken() == null){
                                result = "token参数不能为空，用户未登录！";
                            }else {
                                result = new EncryptFileLogin().topLogin(userInfo);
                            }
                        }
                    } else if (eventJson.getJSONObject("queryString").getString("saveData") != null &&
                            eventJson.getJSONObject("queryString").getString("key") == null && eventJson.getJSONObject("queryString").getString("pwd") == null && eventJson.getJSONObject("queryString").getString("userId") == null && eventJson.getJSONObject("queryString").getString("authKey") == null && eventJson.getJSONObject("queryString").getString("secretKey") == null) {
                        try {
                            String saveData = new TripleDES().decryptMode(eventJson.getJSONObject("queryString").getString("saveData"));
                            JSONObject saveDataJson = JSONObject.parseObject(saveData);
                            UserInfo userInfo = new UserInfo();
                            if(eventJson.getJSONObject("queryString").getString("newpwd") != null){
                                userInfo.setNewPass(eventJson.getJSONObject("queryString").getString("newpwd"));
                            }
                            if(eventJson.getJSONObject("queryString").getString("token") != null){
                                userInfo.setToken(eventJson.getJSONObject("queryString").getString("token"));
                            }
                            userInfo.setUserId(saveDataJson.getString("userId"));
                            userInfo.setAuthKey(saveDataJson.getString("authKey"));
                            userInfo.setSecretKey(saveDataJson.getString("secretKey"));
                            userInfo.setCookie(new BasicCookieStore());
                            Random rand = new Random(); //创建一个随机产生数类Scanner
                            int port = rand.nextInt(2099 - 100 + 1) + 100;
                            userInfo.setPort("ip"+port);
                            long nowTime = System.currentTimeMillis() / 1000L;
                            String itunes = new GetRequest().sendGet(userInfo,"https://itunes.apple.com/us/lookup?bundleId=com.aniplex.fategrandorder.en&time="+nowTime);
                            JSONObject res = JSONObject.parseObject(itunes);
                            appVer = res.getJSONArray("results").getJSONObject(0).getString("version");
                            itunes = new GetRequest().sendGet2(userInfo,"https://game.fate-go.us/gamedata/top?appVer="+appVer);
                            res = JSONObject.parseObject(itunes);
                            if (res.getJSONArray("response").getJSONObject(0).getJSONObject("fail").containsKey("action") && res.getJSONArray("response").getJSONObject(0).getJSONObject("fail").getString("action").equals("maint")) {
                                result = res.getJSONArray("response").getJSONObject(0).getJSONObject("fail").getString("detail");
                            }else{
                                dataVer = res.getJSONArray("response").getJSONObject(0).getJSONObject("success").getString("dataVer");
                                dateVer = res.getJSONArray("response").getJSONObject(0).getJSONObject("success").getString("dateVer");
                                String assetbundle = res.getJSONArray("response").getJSONObject(0).getJSONObject("success").getString("assetbundle");
                                Map<String,Object> map = mouseInfoMsgPack(Base64.getDecoder().decode(assetbundle));
                                assetbundleFolder = (String) map.get("folderName");
                                CRC32 crc32 = new CRC32();
                                crc32.update(assetbundleFolder.getBytes(StandardCharsets.UTF_8));
                                dataServerFolderCrc = String.valueOf(crc32.getValue());
                                animalName = (String) map.get("animalName");
                                byte[] a = EncryptFile.animalName.getBytes();
                                key = new byte[32];
                                for (int i = 0; i < 32; i++) {
                                    key[i] = (byte) (a[i] ^ 4);
                                }
                                iv = new byte[a.length - 32];
                                for (int i = 0; i < a.length - 32; i++) {
                                    iv[i] = (byte) (a[i + 32] ^ 8);
                                }
                                if(userInfo.getToken() == null){
                                    result = "token参数不能为空，用户未登录！";
                                }else {
                                    result = new EncryptFileLogin().topLogin(userInfo);
                                }
                            }
                        } catch (Exception e) {
                            result = "存档码解析错误！";
                        }
                    } else {
                        result = "请求参数错误！";
                    }
                }else{
                    result = "目前仅支持jp、us区服！";
                }
            }else{
                result = "未指定区服！";
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
            result = "请使用GET请求方式！";
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
    public static Map<String,Object> mouseInfoMsgPack(byte[] data){
        byte[] InfoTop = new byte[32];
        byte[] array = new byte[data.length - 32];
        byte[] infoData = mouseInfoMsgPackKey.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(data, 0, InfoTop, 0, 32);
        System.arraycopy(data, 32, array, 0, data.length - 32);
        return mouseHomeMsgPack(array, infoData, InfoTop);
    }
    public static Map<String,Object> mouseHomeMsgPack(byte[] data, byte[] home, byte[] info){
        Map<String,Object> a = decryptWithAesCBC(home,info,data);
        return a;
    }
}