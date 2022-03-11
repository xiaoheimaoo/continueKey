package cn.mcfun.utils;

import cn.mcfun.entity.UserInfo;
import cn.mcfun.request.GetRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.http.impl.client.BasicCookieStore;

import java.nio.charset.StandardCharsets;
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
        JSONObject eventJson = JSONObject.parseObject(event);
        String result;
        if(eventJson.getJSONObject("queryString").getString("key") == null || eventJson.getJSONObject("queryString").getString("pwd") == null || eventJson.getJSONObject("queryString").getString("key").length() != 10 || eventJson.getJSONObject("queryString").getString("pwd").length() < 4){
            result = "请求参数错误！";
        } else{
            UserInfo userInfo = new UserInfo();
            if(eventJson.getJSONObject("queryString").getString("continuePass") != null){
                userInfo.setContinuePass(eventJson.getJSONObject("queryString").getString("continuePass"));
            }
            userInfo.setCookie(new BasicCookieStore());
            String get = new GetRequest().sendGet(userInfo,"https://raw.githubusercontent.com/xiaoheimaoo/FGOData/master/mstVer.json");
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
            for (int i=0;i<32; i++) {
                key[i] = (byte)(a[i] ^ 4);
            }
            iv = new byte[a.length-32];
            for (int i=0;i<a.length-32; i++) {
                iv[i] = (byte)(a[i+32] ^ 8);
            }
            userInfo.setKey(eventJson.getJSONObject("queryString").getString("key"));
            userInfo.setPass(eventJson.getJSONObject("queryString").getString("pwd"));
            result = new ContinueKeyLogin().regist(userInfo);
        }
        JSONObject json = new JSONObject();
        json.put("isBase64Encoded",false);
        json.put("statusCode",200);
        json.put("headers",JSONObject.parseObject("{\"Content-Type\":\"application/json;charset=utf-8\"}"));
        try{
            json.put("body", JSON.toJSONString(JSONObject.parseObject(result), SerializerFeature.PrettyFormat));
        } catch (Exception e){

        } finally {
            json.put("body", result);
        }
        System.out.println("--------------------记录数据3--------------------");
        System.out.println(result);
        System.out.println("------------------------------------------------------");
        return json.toJSONString();
    }
}