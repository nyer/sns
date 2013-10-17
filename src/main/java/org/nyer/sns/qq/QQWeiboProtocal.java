package org.nyer.sns.qq;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.nyer.sns.core.AbstractWeiboProtocal;
import org.nyer.sns.core.WeiboResponse;
import org.nyer.sns.http.CachedHttpResponse;
import org.nyer.sns.http.PrepareRequest;
import org.nyer.sns.oauth.OAuth;
import org.nyer.sns.token.OAuthTokenPair;

public class QQWeiboProtocal extends AbstractWeiboProtocal {
	private static final String ERRCODE = "errcode";

    private static final String RET = "ret";

    private OAuth oauth;
	public QQWeiboProtocal(OAuth oAuth) {
		super(oAuth.getEndPoint());
		this.oauth = oAuth;
	}
	
	@Override
	public WeiboResponse process(PrepareRequest request) {
		WeiboResponse weiboResponse = new WeiboResponse();
        try {
    		CachedHttpResponse response = request.send();

    		String responseText = response.getResponseText();
    		weiboResponse.setHttpResponseText(responseText);
    		weiboResponse.setHttpStatus(response.getStatusCode());
    		
            JSONObject jsonObject = JSONObject.fromObject(responseText);
            if (!jsonObject.has(ERRCODE)) {
                return weiboResponse;
            }

            int errcode = jsonObject.getInt(ERRCODE);
            if (errcode == 0) {
                return weiboResponse;
            }

            weiboResponse.setStatus(OTHER_ERROR);
            if (jsonObject.has(RET)) {
                int ret = jsonObject.getInt(RET);
                if (ret == 4) {
                    if (errcode == 13) {
                    	weiboResponse.setStatus(REPEAT_ERROR);
                    } else if (errcode == 10) {
                    	weiboResponse.setStatus(FREQUENCY_ERROR);
                    } else if (errcode == 4) {
                    	weiboResponse.setStatus(BAN_WORD_ERROR);
                    }
                } else if (ret == 3) {
                    if (errcode != 0) {
                    	weiboResponse.setStatus(AUTH_ERROR);
                    }
                } else if (ret == 5) {
                	// 用户误操作的错误，一般由于本地严格控制，不会出现，要出现的话也是有意的，比如
                	// 拉取粉丝，好友列表，由于QQ没有下一页数据会返回5的错误，这里当作是成功的响应
                	weiboResponse.setStatus(SUCCESS);
                }
            }
        } catch (Exception e) {
        	weiboResponse.setLocalError(e);
        }
		return weiboResponse;
	}

	@Override
	public WeiboResponse get(String baseUri,
			Map<String, String> additionalParams, OAuthTokenPair accessToken) {
		Map<String, String> params = generateCommonParams(accessToken);
		params.putAll(additionalParams);
		
		return super.get(baseUri, params, accessToken);
	}
	
	@Override
	public WeiboResponse post(String baseUri,
			Map<String, String> additionalParams, HttpEntity requestEntity,
			OAuthTokenPair accessToken) {
		Map<String, String> params = generateCommonParams(accessToken);
		params.putAll(additionalParams);
		
		return super.post(baseUri, params, requestEntity, accessToken);
	}
	
	private Map<String, String> generateCommonParams(OAuthTokenPair accessTokenPair) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("oauth_consumer_key", oauth.getDeveloperAccount().getKey());
        params.put("oauth_version", "2.a");
        params.put("access_token", accessTokenPair.getToken());
        params.put("scope", "all");
        params.put("openid", accessTokenPair.getUid() + "");

        return params;
    }
}
