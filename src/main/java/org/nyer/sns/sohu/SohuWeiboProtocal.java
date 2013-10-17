package org.nyer.sns.sohu;

import net.sf.json.JSONObject;

import org.nyer.sns.core.AbstractWeiboProtocal;
import org.nyer.sns.core.WeiboResponse;
import org.nyer.sns.http.CachedHttpResponse;
import org.nyer.sns.http.PrepareRequest;
import org.nyer.sns.oauth.OAuthEndPoint;

public class SohuWeiboProtocal extends AbstractWeiboProtocal {

	public SohuWeiboProtocal(OAuthEndPoint endPoint) {
		super(endPoint);
	}

	@Override
	public WeiboResponse process(PrepareRequest request) {
		WeiboResponse weiboResponse = new WeiboResponse();
        try {
    		CachedHttpResponse response = request.send();
    		weiboResponse.setHttpStatus(response.getStatusCode());
    		weiboResponse.setHttpResponseText(response.getResponseText());
    		
    		String responseText = response.getResponseText();
    		if (response.isStatusCodeOK()) {
        		if (isJsonArray(responseText) == false) {
                    JSONObject jsonObject = JSONObject.fromObject(responseText);
                    if (jsonObject.has("error")) {
                        String error = jsonObject.getString("error");
                        if (error.contains("Same status is not acceptable within 5 minutes.")) {
                        	weiboResponse.setStatus(REPEAT_ERROR);
                        } else if (error.contains("This method requires authentication.")) {
                        	weiboResponse.setStatus(AUTH_ERROR);
                        } else {
                        	weiboResponse.setStatus(OTHER_ERROR);
                        }
                    }
        		}
    		} else {
            	weiboResponse.setStatus(OTHER_ERROR);
    		}
        } catch (Exception e) {
        	weiboResponse.setLocalError(e);
        }
		return weiboResponse;
	}
}
