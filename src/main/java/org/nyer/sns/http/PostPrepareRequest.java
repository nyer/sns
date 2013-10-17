package org.nyer.sns.http;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;

public class PostPrepareRequest extends AbstractPrepareRequest {

    public PostPrepareRequest(HttpClient httpClient, String url) {
        super(httpClient, url);
    }

    @Override
    public CachedHttpResponse send() throws IOException {
        HttpPost httpPost = createHttpPost();
        HttpResponse resp = null;
        try {
            resp = httpClient.execute(httpPost);
            return new CachedHttpResponse(resp.getStatusLine().getStatusCode(), EntityUtils.toString(resp.getEntity(),
                getCharset()), resp.getAllHeaders());
        } finally {
            httpPost.releaseConnection();
        }
    }
}
