package org.nyer.sns.core;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.nyer.sns.oauth.OAuthEndPoint;
import org.nyer.sns.token.OAuthTokenPair;

public abstract class AbstractWeiboProtocal implements WeiboProtocal {
	private OAuthEndPoint endPoint;
	
	public AbstractWeiboProtocal(OAuthEndPoint endPoint) {
		this.endPoint = endPoint;
	}
	
	@Override
	public WeiboResponse post(String baseUri,
			Map<String, String> additionalParams, HttpEntity requestEntity,
			OAuthTokenPair accessToken) {
		if (requestEntity != null && additionalParams != null 
				&& requestEntity instanceof MultipartEntity) {
			MultipartEntity multi = (MultipartEntity) requestEntity;
			for (Entry<String, String> entry : additionalParams.entrySet()) {
	            try {
	            	multi.addPart(entry.getKey(), new StringBody(entry.getValue(), Consts.UTF_8));
	            } catch (UnsupportedEncodingException e) {
	                throw new RuntimeException(e);
	            }
	        }
			
			return process(this.endPoint.post(baseUri, null, multi, accessToken));
		}
		
		return process(this.endPoint.post(baseUri, additionalParams, requestEntity, accessToken));
	}
	
	@Override
	public WeiboResponse post(String baseUri,
			Map<String, String> additionalParams, OAuthTokenPair accessToken) {
		return post(baseUri, additionalParams, null, accessToken);
	}
	
	@Override
	public WeiboResponse post(String baseUri, HttpEntity requestEntity,
			OAuthTokenPair accessToken) {
		return post(baseUri, null, requestEntity, accessToken);
	}
	
	@Override
	public WeiboResponse post(String baseUri, OAuthTokenPair accessToken) {
		return post(baseUri, null, null, accessToken);
	}
	
	@Override
	public WeiboResponse get(String baseUri,
			Map<String, String> additionalParams, OAuthTokenPair accessToken) {
		return process(endPoint.get(baseUri, additionalParams, accessToken));
	}
	
	@Override
	public WeiboResponse get(String baseUri,
			Map<String, String> additionalParams) {
		return get(baseUri, additionalParams, null);
	}
	
	@Override
	public WeiboResponse get(String baseUri, OAuthTokenPair accessToken) {
		return get(baseUri, null, accessToken);
	}
	
	@Override
	public OAuthEndPoint getEndPoint() {
		return this.endPoint;
	}

	@Override
	public void setEndPoint(OAuthEndPoint endPoint) {
		this.endPoint = endPoint;
	}


	protected boolean isJsonArray(String str) {
		return str.trim().startsWith("[");
	}
}
