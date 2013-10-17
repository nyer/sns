package org.nyer.sns.oauth.v2;


import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.CoreProtocolPNames;
import org.nyer.sns.http.PrepareHttpClient;
import org.nyer.sns.http.PrepareRequest;
import org.nyer.sns.oauth.OAuthConstants;
import org.nyer.sns.token.OAuthTokenPair;

public class OAuth2EndPointImpl implements OAuth2EndPoint, OAuthConstants {
    private PrepareHttpClient httpClient;
	public OAuth2EndPointImpl(PrepareHttpClient httpClient) {
		this.httpClient = httpClient;
	}
	
	@Override
	public PrepareRequest post(String baseUri,
			Map<String, String> additionalParams,
			HttpEntity requestEntity, OAuthTokenPair accessToken) {
		PrepareRequest req = httpClient.preparePost(baseUri);
		req.httpParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, "UTF-8");
		doCommon(req, additionalParams, accessToken);
		if (requestEntity != null)
			req.requestEntity(requestEntity);
		
		return req;
	}
	
	@Override
	public PrepareRequest post(String baseUri,
			Map<String, String> additionalParams, OAuthTokenPair accessToken) {
		return post(baseUri, additionalParams, null, accessToken);
	}
	

	@Override
	public PrepareRequest post(String baseUri, HttpEntity requestEntity,
			OAuthTokenPair accessToken) {
		return post(baseUri, null, requestEntity, accessToken);
	}

	@Override
	public PrepareRequest post(String baseUri, OAuthTokenPair accessToken) {
		return post(baseUri, (Map<String, String>)null, accessToken);
	}

	@Override
	public PrepareRequest get(String baseUri,
			Map<String, String> additionalParams, OAuthTokenPair accessToken) {
		PrepareRequest req = httpClient.prepareGet(baseUri);
		doCommon(req, additionalParams, accessToken);

		return req;
	}

	@Override
	public PrepareRequest get(String baseUri,
			Map<String, String> additionalParams) {
		return get(baseUri, additionalParams, null);
	}
	
	@Override
	public PrepareRequest get(String baseUri, OAuthTokenPair accessToken)  {
		return get(baseUri, null, accessToken);
	}
	
	private void doCommon(PrepareRequest req, Map<String, String> additionalParams, OAuthTokenPair accessToken) {
		if (accessToken != null) {
			req.header(new BasicHeader("Authorization", "OAuth2 " + accessToken.getToken()));
        }
		if (additionalParams != null) {
			for (Entry<String, String> entry : additionalParams.entrySet()) {
	            req.parameter(entry.getKey(), entry.getValue());
	        }
		}
	}
}
