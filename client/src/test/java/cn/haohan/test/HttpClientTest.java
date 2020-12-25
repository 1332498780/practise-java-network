package cn.haohan.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.junit.Test;

import java.io.*;

@Slf4j
public class HttpClientTest {

    @Test
    public void testHttpGet(){

        HttpUriRequest request = new HttpGet("http://www.baidu.com/");
        try(CloseableHttpClient client = HttpClientBuilder.create().build();
            CloseableHttpResponse response = client.execute(request)){
            log.info("status-line:{}",response.getStatusLine().toString());

            HttpEntity entity = response.getEntity();
            InputStream inputStream = entity.getContent();
            InputStreamReader reader = new InputStreamReader(inputStream,"UTF-8");
            char[] chars = new char[1024];
            int readCount = 0;
            while((readCount = reader.read(chars))!=-1){
                log.info("read:{}",new String(chars,0,readCount));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
