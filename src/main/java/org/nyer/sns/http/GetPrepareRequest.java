package org.nyer.sns.http;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

public class GetPrepareRequest extends AbstractPrepareRequest {

    public GetPrepareRequest(HttpClient httpClient, String url) {
        super(httpClient, url);
    }

    @Override
    public CachedHttpResponse send() throws IOException {
        HttpGet httpGet = createHttpGet();
        HttpResponse resp = null;
        try {
            resp = httpClient.execute(httpGet);
            return new CachedHttpResponse(resp.getStatusLine().getStatusCode(), EntityUtils.toString(resp.getEntity(),
                getCharset()), resp.getAllHeaders());
        } finally {
            httpGet.releaseConnection();
        }
    }
}
