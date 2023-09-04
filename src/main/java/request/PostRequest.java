package request;

import crypt.MD5;
import entity.UserInfo;
import org.apache.http.HttpHost;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.UUID;

import static umamusume.Main.appVer;


public class PostRequest {

    public static String sendPost(UserInfo userInfo, String url, List<BasicNameValuePair> params) {
        HttpHost proxy;
        proxy = new HttpHost("127.0.0.1", 8888);
        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
        CloseableHttpClient httpClientBuilder = HttpClientBuilder.create().setDefaultCookieStore(new BasicCookieStore())
                //.setRoutePlanner(routePlanner)
                .build();
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Connection", "keep-alive");
        httpPost.addHeader("Accept", "*/*");
        httpPost.addHeader("User-Agent","Mozilla/5.0 BSGameSDK");
        CloseableHttpResponse response = null;
        String result = null;
        int num = 0;
        while(num == 0){
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
                response = httpClientBuilder.execute(httpPost);
                result = EntityUtils.toString(response.getEntity(), Charset.forName("utf-8"));
                num = 1;
            } catch (IOException e) {
                num = 0;
            }
        }
        return result;
    }
    public static String sendPost(UserInfo userInfo, String url, String params) {
        HttpHost proxy;
        proxy = new HttpHost("127.0.0.1", 8888);
        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
        CloseableHttpClient httpClientBuilder = HttpClientBuilder.create().setDefaultCookieStore(new BasicCookieStore())
                //.setRoutePlanner(routePlanner)
                .build();
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Host", "le1-prod-bili-gs-uma.bilibiligame.net");
        httpPost.addHeader("APP-VER", appVer);
        httpPost.addHeader("X-Unity-Version", "2020.3.48f1");
        httpPost.addHeader("Accept", "*/*");
        httpPost.addHeader("BUMA-OPEN-ID", userInfo.getBUMA_OPEN_ID());
        httpPost.addHeader("BUMA-RID", MD5.encrypt(UUID.randomUUID().toString()));
        httpPost.addHeader("ViewerID", userInfo.getViewerID());
        httpPost.addHeader("Content-Type", "application/x-msgpack");
        httpPost.addHeader("SID", userInfo.getSid());
        httpPost.addHeader("X-Ba-Catch-Control", "no-cache");
        httpPost.addHeader("User-Agent", "umamusu/9260 CFNetwork/1402.0.8 Darwin/22.2.0");
        httpPost.addHeader("X-Ba-Charset", "utf8");
        httpPost.addHeader("BX-Accept-Language", "zh");
        httpPost.addHeader("RES-VER", userInfo.getRES_VER());
        httpPost.addHeader("Device", "1");
        httpPost.addHeader("Connection", "keep-alive");
        CloseableHttpResponse response = null;
        String result = null;
        int num = 0;
        while(num == 0){
            try {
                httpPost.setEntity(new StringEntity(params));
                response = httpClientBuilder.execute(httpPost);
                result = EntityUtils.toString(response.getEntity(), Charset.forName("utf-8"));
                num = 1;
            } catch (IOException e) {
                num = 0;
            }
        }
        return result;
    }

}
