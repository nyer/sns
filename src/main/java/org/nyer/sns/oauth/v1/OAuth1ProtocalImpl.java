package org.nyer.sns.oauth.v1;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.nyer.sns.http.CachedHttpResponse;
import org.nyer.sns.http.util.UrlBuilder;
import org.nyer.sns.http.util.UrlUtil;
import org.nyer.sns.oauth.OAuthConstants;

/**
 * OAuth1服务协议实现
 * @author leiting
 *
 */
public class OAuth1ProtocalImpl implements OAuth1Protocal, OAuthConstants {
    private static final Logger log = Logger.getLogger(LOG_NAME);
    
	private OAuth1 oauth;
	private OAuth1EndPoint oauthEndPoint;
	
	public OAuth1ProtocalImpl(OAuth1 oauth, OAuth1EndPoint oauthEndPoint) {
		this.oauth = oauth;
		this.oauthEndPoint = oauthEndPoint;
	}
	
	@Override
	public String authorizeUrl(OAuth1TokenPair requestTokenPair, String from, String scope) {
		UrlBuilder builder = new UrlBuilder(oauth.getDeveloperAccount().getCallbackUri());
        if (StringUtils.isNotBlank(from)) {
            builder.add("from", from);
        }
        builder.add("scope", scope);
        String callbackUrl = builder.toUrl("UTF-8");
        
        return new UrlBuilder(oauth.getUriAuthorize()).add("oauth_token", requestTokenPair.getToken()).add("oauth_callback",
                callbackUrl).toUrl("UTF-8");
	}

	@Override
	public OAuth1TokenPair getAccessToken(String oauthVerifier, OAuth1TokenPair requestTokenPair) {
		Map<String, String> additionalParams = new HashMap<String, String>();
		String baseUri = oauth.getUriAccessToken();
		if (StringUtils.isNotBlank(oauthVerifier)) {
			additionalParams.put("oauth_verifier", oauthVerifier);
        }
		
		try {
			CachedHttpResponse resp = oauthEndPoint.post(baseUri,
					additionalParams, requestTokenPair).send();
			if (resp.isStatusCodeOK()) {
				Map<String, String> map = UrlUtil.parseUrlParams(resp.getResponseText());
	            String accessToken = map.get("oauth_token");
	            String accessTokenSecret = map.get("oauth_token_secret");
	            if (StringUtils.isNotBlank(accessToken) && StringUtils.isNotBlank(accessTokenSecret)) {
	                return new OAuth1TokenPair(accessToken, accessTokenSecret);
	            }
			}

            log.error("Unable to get access token, baseUri: " + baseUri + ", status code: " + resp.getStatusCode() +
                ", response text: " + resp.getResponseText());
		} catch (Exception e) {
			log.error("error to get access token, uri: " + requestTokenPair);
		}
		return null;
	}

	@Override
	public OAuth1TokenPair getRequestToken() {
		Map<String, String> additionalParams = new HashMap<String, String>();
		String baseUri = oauth.getUriRequestToken();
		additionalParams.put("oauth_callback", "null");
		try {
			CachedHttpResponse httpResponse = oauthEndPoint.get(baseUri, additionalParams).send();
			int statusCode = httpResponse.getStatusCode();
			String resp = httpResponse.getResponseText();
			
			Map<String, String> map = UrlUtil.parseUrlParams(resp);
            String requestToken = map.get("oauth_token");
            String requestTokenSecret = map.get("oauth_token_secret");
            if (StringUtils.isNotBlank(requestToken) && StringUtils.isNotBlank(requestTokenSecret)) {
                return new OAuth1TokenPair(requestToken, requestTokenSecret);
            }
            log.error("Unable to get request token, baseUri: " + baseUri + ", status code: " + statusCode +
                    ", response text: " + resp);
		} catch (Exception e) {
			log.error("error to get request token, uri: " + oauth.getUriRequestToken(), e);
		}
		return null;
	}
}
