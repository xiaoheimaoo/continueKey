package cn.mcfun.utils;

import cn.mcfun.entity.UserInfo;
import cn.mcfun.request.PostRequest;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.message.BasicNameValuePair;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class EncryptFileLogin {
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
            String rs = jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("fail").getString("detail");
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
            String rs = jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("fail").getString("detail");
            return rs;
        }

    }
    //生成新的引继码
    public static String prepare(UserInfo userInfo) {
        String lastAccessTime = String.valueOf(System.currentTimeMillis() / 1000L);
        List<BasicNameValuePair> params = new ArrayList();
        int continuePass = new Random().nextInt(9999) % (9999 - 1000 + 1) + 1000;
        userInfo.setContinuePass(String.valueOf(continuePass));
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
            String json = "{\"continueKey\": \""+userInfo.getContinueKey()+"\", \"continuePass\": \""+userInfo.getContinuePass()+"\", \"userId\": \""+userInfo.getUserId()+"\"}";
            return json;
        } else {
            String rs = jsonObject.getJSONArray("response").getJSONObject(0).getJSONObject("fail").getString("detail");
            return rs;
        }

    }
}
