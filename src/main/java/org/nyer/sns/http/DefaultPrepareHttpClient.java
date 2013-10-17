package org.nyer.sns.http;

import org.apache.http.client.HttpClient;
import org.nyer.sns.http.util.HttpUtil;

public class DefaultPrepareHttpClient implements PrepareHttpClient {

	private HttpClient httpClient;
    
	public DefaultPrepareHttpClient() {
		this.httpClient = HttpUtil.makeHttpClient(500, 10, 30);
	}
	
    public PrepareRequest prepareGet(String url) {
        return new GetPrepareRequest(httpClient, url);
    }
    
    public PrepareRequest preparePost(String url) {
        return new PostPrepareRequest(httpClient, url);
    }
}
