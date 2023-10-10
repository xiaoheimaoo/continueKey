package cn.mcfun.request;

import java.io.IOException;
import java.nio.charset.Charset;

import cn.mcfun.entity.UserInfo;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.util.EntityUtils;

public class GetRequest {
    public String sendGet(UserInfo userInfo, String url) {
        CloseableHttpClient httpClient;
        HttpHost proxy;
        proxy = new HttpHost("xxx.xxx.xxx.xxx",8888);
        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
        httpClient = HttpClients.custom()
                .setDefaultCookieStore(userInfo.getCookie())
                .setRoutePlanner(routePlanner)
                .build();
        HttpGet httpGet = new HttpGet(url);
        String result = null;

        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            result = EntityUtils.toString(response.getEntity(), Charset.forName("utf-8"));
        } catch (IOException var7) {
            var7.printStackTrace();
        }

        return result;
    }
    public String sendGet2(UserInfo userInfo, String url) {
        CloseableHttpClient httpClient;
        HttpHost proxy;
        proxy = new HttpHost("xxx.xxx.xxx.xxx",8888);
        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
        httpClient = HttpClients.custom()
                .setDefaultCookieStore(userInfo.getCookie())
                //.setRoutePlanner(routePlanner)
                .build();
        HttpGet httpGet = new HttpGet(url);
        String result = null;

        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            result = EntityUtils.toString(response.getEntity(), Charset.forName("utf-8"));
        } catch (IOException var7) {
            var7.printStackTrace();
        }

        return result;
    }
}
