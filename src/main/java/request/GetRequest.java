package request;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.Charset;

public class GetRequest {
    static CloseableHttpClient httpClient = HttpClients.createDefault();
    public static String sendGet(String url) {
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
    public static String getHash(String url) {
        HttpGet httpGet = new HttpGet(url);
        String result = null;

        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            result = EntityUtils.toString(response.getEntity(), Charset.forName("utf-8"));
        } catch (IOException var7) {
            var7.printStackTrace();
        }
        result = result.split("\"currentOid\":\"")[1].split("\"},")[0];
        return result;
    }
}
