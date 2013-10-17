package org.nyer.sns.renren;

import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.nyer.sns.core.AbstractWeiboProtocal;
import org.nyer.sns.core.WeiboResponse;
import org.nyer.sns.http.CachedHttpResponse;
import org.nyer.sns.http.PrepareRequest;
import org.nyer.sns.oauth.OAuth;
import org.nyer.sns.token.OAuthTokenPair;

public class RenRenProtocal extends AbstractWeiboProtocal {
    private static final String ERROR_CODE = "error_code";
    
	public RenRenProtocal(OAuth oAuth) {
		super(oAuth.getEndPoint());
	}

	@Override
	public WeiboResponse process(PrepareRequest request) {
		WeiboResponse weiboResponse = new WeiboResponse();
        try {
    		CachedHttpResponse response = request.send();

    		String responseText = response.getResponseText();
    		weiboResponse.setHttpResponseText(responseText);
    		weiboResponse.setHttpStatus(response.getStatusCode());
    		
    		if (response.isStatusCodeOK()) {
    			if (isJsonArray(responseText) == false) {
    				JSONObject jsonObject = JSONObject.fromObject(responseText);
    	            if (jsonObject.has(ERROR_CODE)) {
    	                weiboResponse.setStatus(OTHER_ERROR);
    	                
    	                int errorCode = jsonObject.getInt(ERROR_CODE);
    	                if (errorCode == 202 || errorCode == 2001 || errorCode == 2002) {
    	                	weiboResponse.setStatus(AUTH_ERROR);
    	                } else if (errorCode == 0) {
    	                	weiboResponse.setStatus(SUCCESS);
    	                } else if (errorCode == 1) {
    	                	weiboResponse.setStatus(REPEAT_ERROR);
    	                } else if (errorCode == 10400) {
    	                	weiboResponse.setStatus(FREQUENCY_ERROR);
    	                } else if (errorCode == 10402) {
    	                	weiboResponse.setStatus(BAN_WORD_ERROR);
    	                }
    	            }
    			}
    		}
        } catch (Exception e) {
        	weiboResponse.setLocalError(e);
        }
        
		return weiboResponse;
	}
	
	@Override
	public WeiboResponse post(String baseUri,
			Map<String, String> additionalParams, HttpEntity requestEntity,
			OAuthTokenPair accessToken) {
		additionalParams.put("v", "1.0");
		additionalParams.put("access_token", accessToken.getToken());
		additionalParams.put("format", "JSON");
		
		return super.post(baseUri, additionalParams, requestEntity, accessToken);
	}
	
	@Override
	public WeiboResponse get(String baseUri,
			Map<String, String> additionalParams, OAuthTokenPair accessToken) {
		additionalParams.put("v", "1.0");
		additionalParams.put("access_token", accessToken.getToken());
		additionalParams.put("format", "JSON");
		
		return super.get(baseUri, additionalParams, accessToken);
	}
}
