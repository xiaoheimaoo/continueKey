package cn.mcfun.request;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import cn.mcfun.entity.UserInfo;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class PostRequest {
    public String sendPost(UserInfo userInfo, String url, List<BasicNameValuePair> params) {
        CloseableHttpClient httpClient;
        HttpHost proxy;
        proxy = new HttpHost("xxx.xxx.xxx.xxx",8888);
        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
        CredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(new AuthScope(proxy), new UsernamePasswordCredentials(userInfo.getPort(), "test"));
        httpClient = HttpClients.custom()
                .setDefaultCookieStore(userInfo.getCookie())
                .setDefaultCredentialsProvider(provider)
                .setRoutePlanner(routePlanner)
                .build();
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Connection", "keep-alive");
        httpPost.addHeader("Accept", "*/*");
        httpPost.addHeader("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 11; Pixel 5 Build/RD1A.201105.003.A1)");
        CloseableHttpResponse response = null;
        String result = null;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
            response = httpClient.execute(httpPost);
            result = EntityUtils.toString(response.getEntity(), Charset.forName("utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
