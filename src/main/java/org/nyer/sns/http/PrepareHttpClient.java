package org.nyer.sns.http;


public interface PrepareHttpClient {
	public PrepareRequest prepareGet(String url);
    
    public PrepareRequest preparePost(String url);
}
