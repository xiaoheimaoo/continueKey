package cn.mcfun.utils;

import cn.mcfun.entity.UserInfo;
import cn.mcfun.request.PostRequest;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 引继码登录流程
 * 该登录流程中出现任何异常都可能会导致账号丢失！请自行处理异常或者及时存档，避免异常终止导致数据丢失！
 */
public class ContinueKeyLogin {
    //注册一个新账号
    public static String regist(UserInfo userInfo) {
        String lastAccessTime = String.valueOf(System.currentTimeMillis() / 1000L);
        List<BasicNameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("appVer", EncryptFile.appVer));
        params.add(new BasicNameValuePair("dateVer", EncryptFile.dateVer));
        params.add(new BasicNameValuePair("dataVer", EncryptFile.dataVer));
        params.add(new BasicNameValuePair("idempotencyKey", UUID.randomUUID().toString().toLowerCase()));
        params.add(new BasicNameValuePair("lastAccessTime", lastAccessTime));
        params.add(new BasicNameValuePair("verCode", "e92c481b51ff8203344cf768b2f5bc84b14409bc2ad7084292c9370ea97621de"));
        String sign = (new AuthCode()).getSign(userInfo, params);
        params.add(new BasicNameValuePair("authCode", sign));
        String result = new PostRequest().sendPost(userInfo, "https://game.fate-go.jp/account/regist", params);
        JSONObject jsonObject = JSONObject.parseObject(result);
        if (jsonObject.getJSONArray("response").getJSONObject(0).getString("resCode").equals("00")) {
            String authKey = jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("success").getString("authKey");
            String secretKey = jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("success").getString("secretKey");
            String userId = jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("success").getString("userId");
            userInfo.setAuthKey(authKey);
            userInfo.setSecretKey(secretKey);
            userInfo.setUserId(userId);
            return decide(userInfo);
        } else {
            File readFile = new File("/mnt/"+new TripleDES().stringToMD5(userInfo.getKey()+userInfo.getPass()+":"+userInfo.getToken()));
            String rs = jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("fail").getString("detail");
            if(readFile.exists()) {
                try {
                    StringBuffer sb = new StringBuffer();
                    InputStream is = new FileInputStream(readFile);
                    String line;
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    line = reader.readLine();
                    while (line != null) {
                        sb.append(line);
                        sb.append("\n");
                        line = reader.readLine();
                    }
                    reader.close();
                    is.close();
                    System.out.println("读取文件成功！");
                    rs = sb.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return rs;
        }

    }
    //使用引继码替换新账号
    public static String decide(UserInfo userInfo) {
        String lastAccessTime = String.valueOf(System.currentTimeMillis() / 1000L);
        List<BasicNameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("continueKey", userInfo.getKey()));
        params.add(new BasicNameValuePair("continuePass", userInfo.getPass()));
        params.add(new BasicNameValuePair("userId", userInfo.getUserId()));
        params.add(new BasicNameValuePair("authKey", userInfo.getAuthKey()));
        params.add(new BasicNameValuePair("appVer", EncryptFile.appVer));
        params.add(new BasicNameValuePair("dateVer", EncryptFile.dateVer));
        params.add(new BasicNameValuePair("dataVer", EncryptFile.dataVer));
        params.add(new BasicNameValuePair("idempotencyKey", UUID.randomUUID().toString().toLowerCase()));
        params.add(new BasicNameValuePair("continueType", "1"));
        params.add(new BasicNameValuePair("lastAccessTime", lastAccessTime));
        params.add(new BasicNameValuePair("verCode", "e92c481b51ff8203344cf768b2f5bc84b14409bc2ad7084292c9370ea97621de"));
        String sign = (new AuthCode()).getSign(userInfo, params);
        params.add(new BasicNameValuePair("authCode", sign));
        String result = new PostRequest().sendPost(userInfo, "https://game.fate-go.jp/continue/decide?_userId="+userInfo.getUserId(), params);
        JSONObject jsonObject = JSONObject.parseObject(result);
        if (jsonObject.getJSONArray("response").getJSONObject(0).getString("resCode").equals("00")) {
            String authKey = jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("success").getString("authKey");
            String secretKey = jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("success").getString("secretKey");
            String userId = jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("success").getString("userId");
            userInfo.setAuthKey(authKey);
            userInfo.setSecretKey(secretKey);
            userInfo.setUserId(userId);
            System.out.println("--------------------记录数据1--------------------");
            System.out.println("oldKey="+userInfo.getKey());
            System.out.println("oldPwd="+userInfo.getPass());
            System.out.println("userId="+userId);
            System.out.println("authKey="+authKey);
            System.out.println("secretKey="+secretKey);
            System.out.println("------------------------------------------------------");
            String log = "{\"data\":\"转换过程出现异常！请复制本条结果联系群主找回账号！\",\"oldKey\":\""+userInfo.getKey()+"\",\"oldPwd\":\""+userInfo.getPass()+"\",\"userId\":\""+userId+"\",\"authKey\":\""+authKey+"\",\"secretKey\":\""+secretKey+"\"}";
            File readFile = new File("/mnt/"+new TripleDES().stringToMD5(userInfo.getKey()+userInfo.getPass()+":"+userInfo.getToken()));
            try {
                Writer write = new OutputStreamWriter(new FileOutputStream(readFile), StandardCharsets.UTF_8);
                write.write(log);
                write.flush();
                write.close();
                System.out.println("写入log数据成功！");
            } catch (IOException e) {
                e.printStackTrace();
            }
            TripleDES des = new TripleDES();
            String file = des.encryptMode("{\"SaveDataVer\":\"Fgo_20150511_1\",\"userCreateServer\":\"game.fate-go.jp/\",\"userId\":\""+userId+"\",\"authKey\":\""+authKey+"\",\"secretKey\":\""+secretKey+"\"}");
            userInfo.setFile(file);
            return topLogin(userInfo);
        } else {
            File readFile = new File("/mnt/"+new TripleDES().stringToMD5(userInfo.getKey()+userInfo.getPass()+userInfo.getPass()+":"+userInfo.getToken()));
            String rs = jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("fail").getString("detail");
            if(readFile.exists()) {
                try {
                    StringBuffer sb = new StringBuffer();
                    InputStream is = new FileInputStream(readFile);
                    String line;
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    line = reader.readLine();
                    while (line != null) {
                        sb.append(line);
                        sb.append("\n");
                        line = reader.readLine();
                    }
                    reader.close();
                    is.close();
                    System.out.println("读取文件成功！");
                    rs = sb.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return rs;
        }

    }
    //登录账号第一步
    public static String topLogin(UserInfo userInfo) {
        String lastAccessTime = String.valueOf(System.currentTimeMillis() / 1000L);
        String userState = String.valueOf(-Long.parseLong(lastAccessTime) >> 2 ^ Long.parseLong(userInfo.getUserId()) & Long.parseLong(EncryptFile.dataServerFolderCrc));
        List<BasicNameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("userId", userInfo.getUserId()));
        params.add(new BasicNameValuePair("authKey", userInfo.getAuthKey()));
        params.add(new BasicNameValuePair("appVer", EncryptFile.appVer));
        params.add(new BasicNameValuePair("dateVer", EncryptFile.dateVer));
        params.add(new BasicNameValuePair("lastAccessTime", lastAccessTime));
        params.add(new BasicNameValuePair("idempotencyKey", UUID.randomUUID().toString().toLowerCase()));
        params.add(new BasicNameValuePair("verCode", "723d93a599b6f10ef3085ff1131fa5679a91da924246b8ca40dded18eccaf3da"));
        params.add(new BasicNameValuePair("userState", userState));
        params.add(new BasicNameValuePair("assetbundleFolder", EncryptFile.assetbundleFolder));
        params.add(new BasicNameValuePair("dataVer", EncryptFile.dataVer));
        params.add(new BasicNameValuePair("isTerminalLogin", "1"));
        String sign = (new AuthCode()).getSign(userInfo, params);
        params.add(new BasicNameValuePair("authCode", sign));
        String result = new PostRequest().sendPost(userInfo, "https://game.fate-go.jp/login/top?_userId=" + userInfo.getUserId(), params);
        JSONObject jsonObject = JSONObject.parseObject(result);
        if (jsonObject.getJSONArray("response").getJSONObject(0).getString("resCode").equals("00")) {
            //登录成功（及时保存存档！）
            return topHome(userInfo);
        } else {
            File readFile = new File("/mnt/"+new TripleDES().stringToMD5(userInfo.getKey()+userInfo.getPass()+userInfo.getPass()+":"+userInfo.getToken()));
            String rs = jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("fail").getString("detail");
            if(readFile.exists()) {
                try {
                    StringBuffer sb = new StringBuffer();
                    InputStream is = new FileInputStream(readFile);
                    String line;
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    line = reader.readLine();
                    while (line != null) {
                        sb.append(line);
                        sb.append("\n");
                        line = reader.readLine();
                    }
                    reader.close();
                    is.close();
                    System.out.println("读取文件成功！");
                    rs = sb.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return rs;
        }

    }
    //登录账号第二步
    public static String topHome(UserInfo userInfo) {
        String lastAccessTime = String.valueOf(System.currentTimeMillis() / 1000L);
        List<BasicNameValuePair> params = new ArrayList();
        params.add(new BasicNameValuePair("userId", userInfo.getUserId()));
        params.add(new BasicNameValuePair("authKey", userInfo.getAuthKey()));
        params.add(new BasicNameValuePair("appVer", EncryptFile.appVer));
        params.add(new BasicNameValuePair("dateVer", EncryptFile.dateVer));
        params.add(new BasicNameValuePair("idempotencyKey", UUID.randomUUID().toString().toLowerCase()));
        params.add(new BasicNameValuePair("lastAccessTime", lastAccessTime));
        params.add(new BasicNameValuePair("verCode", "723d93a599b6f10ef3085ff1131fa5679a91da924246b8ca40dded18eccaf3da"));
        params.add(new BasicNameValuePair("dataVer", EncryptFile.dataVer));
        String sign = (new AuthCode()).getSign(userInfo, params);
        params.add(new BasicNameValuePair("authCode", sign));
        String result = new PostRequest().sendPost(userInfo, "https://game.fate-go.jp/home/top?_userId=" + userInfo.getUserId(), params);
        JSONObject jsonObject = JSONObject.parseObject(result);
        if (jsonObject.getJSONArray("response").getJSONObject(0).getString("resCode").equals("00")) {
            return prepare(userInfo);
        } else {
            File readFile = new File("/mnt/"+new TripleDES().stringToMD5(userInfo.getKey()+userInfo.getPass()+userInfo.getPass()+":"+userInfo.getToken()));
            String rs = jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("fail").getString("detail");
            if(readFile.exists()) {
                try {
                    StringBuffer sb = new StringBuffer();
                    InputStream is = new FileInputStream(readFile);
                    String line;
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    line = reader.readLine();
                    while (line != null) {
                        sb.append(line);
                        sb.append("\n");
                        line = reader.readLine();
                    }
                    reader.close();
                    is.close();
                    System.out.println("读取文件成功！");
                    rs = sb.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return rs;
        }

    }
    //生成新的引继码
    public static String prepare(UserInfo userInfo) {
        String lastAccessTime = String.valueOf(System.currentTimeMillis() / 1000L);
        List<BasicNameValuePair> params = new ArrayList();
        String continuePass = String.valueOf(new Random().nextInt(9999) % (9999 - 1000 + 1) + 1000);
        if(userInfo.getNewPass() != null){
            continuePass = userInfo.getNewPass();
        }
        userInfo.setContinuePass(continuePass);
        params.add(new BasicNameValuePair("continuePass", String.valueOf(continuePass)));
        params.add(new BasicNameValuePair("userId", userInfo.getUserId()));
        params.add(new BasicNameValuePair("authKey", userInfo.getAuthKey()));
        params.add(new BasicNameValuePair("appVer", EncryptFile.appVer));
        params.add(new BasicNameValuePair("dateVer", EncryptFile.dateVer));
        params.add(new BasicNameValuePair("dataVer", EncryptFile.dataVer));
        params.add(new BasicNameValuePair("idempotencyKey", UUID.randomUUID().toString().toLowerCase()));
        params.add(new BasicNameValuePair("lastAccessTime", lastAccessTime));
        params.add(new BasicNameValuePair("verCode", "e92c481b51ff8203344cf768b2f5bc84b14409bc2ad7084292c9370ea97621de"));
        String sign = (new AuthCode()).getSign(userInfo, params);
        params.add(new BasicNameValuePair("authCode", sign));
        String result = new PostRequest().sendPost(userInfo, "https://game.fate-go.jp/continue/prepare?_userId="+userInfo.getUserId(), params);
        JSONObject jsonObject = JSONObject.parseObject(result);
        if (jsonObject.getJSONArray("response").getJSONObject(0).getString("resCode").equals("00")) {
            String continueKey = jsonObject.getJSONObject("cache").getJSONObject("updated").getJSONArray("userContinue").getJSONObject(0).getString("continueKey");
            //打印新的引继码（密码为“continuePass”的值）
            userInfo.setContinueKey(continueKey);
            System.out.println("--------------------记录数据2--------------------");
            System.out.println("continueKey="+continueKey);
            System.out.println("continuePass="+continuePass);
            System.out.println("------------------------------------------------------");
            String json1 = "{\"continueKey\": \""+userInfo.getContinueKey()+"\", \"continuePass\": \""+userInfo.getContinuePass()+"\", \"encryptFile\": \""+userInfo.getFile()+"\", \"userId\": \""+userInfo.getUserId()+"\", \"authKey\": \""+userInfo.getAuthKey()+"\", \"secretKey\": \""+userInfo.getSecretKey()+"\", \"isNew\": false}";
            String json2 = "{\"continueKey\": \""+userInfo.getContinueKey()+"\", \"continuePass\": \""+userInfo.getContinuePass()+"\", \"encryptFile\": \""+userInfo.getFile()+"\", \"userId\": \""+userInfo.getUserId()+"\", \"authKey\": \""+userInfo.getAuthKey()+"\", \"secretKey\": \""+userInfo.getSecretKey()+"\", \"isNew\": true}";
            File readFile = new File("/mnt/"+new TripleDES().stringToMD5(userInfo.getKey()+userInfo.getPass()+userInfo.getPass()+":"+userInfo.getToken()));
            try {
                Writer write = new OutputStreamWriter(new FileOutputStream(readFile), StandardCharsets.UTF_8);
                write.write(json1);
                write.flush();
                write.close();
                System.out.println("写入数据成功！");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return json2;
        } else {
            File readFile = new File("/mnt/"+new TripleDES().stringToMD5(userInfo.getKey()+userInfo.getPass()+userInfo.getPass()+":"+userInfo.getToken()));
            String rs = jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("fail").getString("detail");
            if(readFile.exists()) {
                try {
                    StringBuffer sb = new StringBuffer();
                    InputStream is = new FileInputStream(readFile);
                    String line;
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    line = reader.readLine();
                    while (line != null) {
                        sb.append(line);
                        sb.append("\n");
                        line = reader.readLine();
                    }
                    reader.close();
                    is.close();
                    System.out.println("读取文件成功！");
                    rs = sb.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return rs;
        }

    }

}
