package org.nyer.sns.oauth.v2;


import java.io.IOException;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.nyer.sns.http.CachedHttpResponse;
import org.nyer.sns.http.util.UrlBuilder;
import org.nyer.sns.http.util.UrlUtil;
import org.nyer.sns.oauth.OAuthConstants;
import org.nyer.sns.util.JsonUtil;
import org.nyer.sns.util.NumberUtil;

public class OAuth2ProtocalImpl implements OAuth2Protocal, OAuthConstants {
	private static Logger log = Logger.getLogger(LOG_NAME);
	
	private OAuth2 oauth;
	private OAuth2EndPoint oauthEndPoint;
	
	public OAuth2ProtocalImpl(OAuth2Impl oAuth2Impl, OAuth2EndPoint endPoint) {
		this.oauth = oAuth2Impl;
		this.oauthEndPoint = endPoint;
	}

	@Override
	public String authorizeUrl(String from, String scope) {
        String callbackUrl = getCallBackUrl(from);
        
        return new UrlBuilder(oauth.getUriAuthorize()).add("client_id", oauth.getDeveloperAccount().getKey()).add("response_type",
                "code").add("redirect_uri", callbackUrl).add("scope", scope).toUrl("UTF-8");
	}

	@Override
	public OAuth2TokenPair getAccessToken(String code, String refreshToken, String from) {
		final String baseUri = this.oauth.getUriAccessToken();
		
        String callbackUrl = getCallBackUrl(from);
		String grantType = StringUtils.isNotBlank(code) ? "authorization_code" : "refresh_token";
        UrlBuilder urlBuilder =
            new UrlBuilder(oauth.getUriAccessToken()).add("client_id", oauth.getDeveloperAccount().getKey()).add("client_secret",
            		oauth.getDeveloperAccount().getSecret()).add("grant_type", grantType).add("redirect_uri", callbackUrl);
        
        if (StringUtils.isNotBlank(code)) {
            urlBuilder.add("code", code);
        } else {
            urlBuilder.add("refresh_token", refreshToken);
        }

        try {
            CachedHttpResponse httpResponse = this.oauthEndPoint.post(urlBuilder.toUrl("UTF-8"), null).send();
            int statusCode = httpResponse.getStatusCode();
            String responseText = httpResponse.getResponseText();
            if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {
                if (isJsonObjectLike(responseText)) {
                    JSONObject jsonObject = JSONObject.fromObject(responseText);
                    String accessToken = JsonUtil.getString(jsonObject, "access_token");
                    if (StringUtils.isNotBlank(accessToken)) {
                        int expiresIn = JsonUtil.getInt(jsonObject, "expires_in", -1);
                        String newRefreshToken = JsonUtil.getString(jsonObject, "refresh_token");
                        return new OAuth2TokenPair(jsonObject, accessToken, expiresIn, newRefreshToken);
                    }
                } else {
                    Map<String, String> params = UrlUtil.parseUrlParams(responseText);
                    String accessToken = params.get("access_token");
                    if (StringUtils.isNotBlank(accessToken)) {
                        String newRefreshToken = params.get("refresh_token");
                        int expiresIn = NumberUtil.parseInt(params.get("expires_in"), -1, false);
                        return new OAuth2TokenPair(params, accessToken, expiresIn, newRefreshToken);
                    }
                }
            }
            log.error("Unable to get access token, baseUri: " + baseUri + ", status code: " + statusCode +
                ", response text: " + responseText);
        } catch (IOException e) {
            log.error("Unable to get access token, baseUri: " + baseUri, e);
        }
		return null;
	}

	@Override
	public OAuth2TokenPair getRequestToken() {
		throw new UnsupportedOperationException();
	}

	private String getCallBackUrl(String from) {
		UrlBuilder builder = new UrlBuilder(oauth.getDeveloperAccount().getCallbackUri());
        if (StringUtils.isNotBlank(from)) {
            builder.add("from", from);
        }
        String callbackUrl = builder.toUrl("UTF-8");
        
        return callbackUrl;
	}
	
	private static boolean isJsonObjectLike(String text) {
        return StringUtils.isNotBlank(text) && text.trim().startsWith("{");
    }
}
