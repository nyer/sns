package org.nyer.sns.sina;

import net.sf.json.JSONObject;

import org.nyer.sns.core.AbstractWeiboProtocal;
import org.nyer.sns.core.WeiboResponse;
import org.nyer.sns.http.CachedHttpResponse;
import org.nyer.sns.http.PrepareRequest;
import org.nyer.sns.oauth.OAuthEndPoint;

public class SinaWeiboProtocal extends AbstractWeiboProtocal {

	public SinaWeiboProtocal(OAuthEndPoint endPoint) {
		super(endPoint);
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
            if (jsonObject.has("error_code")) {
            	weiboResponse.setStatus(OTHER_ERROR);
            	
                int error = jsonObject.getInt("error_code");
                if (error == 21327 || error == 21332 || error == 21501) {
                	weiboResponse.setStatus(AUTH_ERROR);
                } else if (error == 20019) {
                	weiboResponse.setStatus(REPEAT_ERROR);
                } else if (error == 20016) {
                	weiboResponse.setStatus(FREQUENCY_ERROR);
                } else if (error == 20021) {
                	weiboResponse.setStatus(BAN_WORD_ERROR);
                }
            }
        } catch (Exception e) {
        	weiboResponse.setLocalError(e);
        }
		return weiboResponse;
	}

}
