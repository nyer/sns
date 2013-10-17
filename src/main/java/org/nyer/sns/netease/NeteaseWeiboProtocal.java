package org.nyer.sns.netease;

import java.io.IOException;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.nyer.sns.core.AbstractWeiboProtocal;
import org.nyer.sns.core.WeiboProtocal;
import org.nyer.sns.core.WeiboResponse;
import org.nyer.sns.http.CachedHttpResponse;
import org.nyer.sns.http.PrepareRequest;
import org.nyer.sns.oauth.OAuthEndPoint;

public class NeteaseWeiboProtocal extends AbstractWeiboProtocal {
	public NeteaseWeiboProtocal(OAuthEndPoint endPoint) {
		super(endPoint);
	}
	
	@Override
	public WeiboResponse process(PrepareRequest request) {
		WeiboResponse response = new WeiboResponse();
		try {
			CachedHttpResponse resp = request.send();
			String responseText = resp.getResponseText();
			response.setHttpResponseText(responseText);
			response.setHttpStatus(resp.getStatusCode());
			try {
                JSONObject jsonObject = JSONObject.fromObject(responseText);
                if (jsonObject.has("message_code")) {
                    String messageCode = jsonObject.getString("message_code");
                    if (messageCode.equals("00401token_invalid")) {
                    	response.setStatus(WeiboProtocal.AUTH_ERROR);
                    } else if (messageCode.endsWith("40324")) {
                    	response.setStatus(WeiboProtocal.REPEAT_ERROR);
                    } else if (messageCode.endsWith("40308")) {
                    	response.setStatus(WeiboProtocal.FREQUENCY_ERROR);
                    } else if (messageCode.endsWith("40306")) {
                    	response.setStatus(WeiboProtocal.BAN_WORD_ERROR);
                    } else {
                    	response.setStatus(WeiboProtocal.OTHER_ERROR);
                    }
                }
            } catch (JSONException e) {
                response.setLocalError(e);
            }
		} catch (IOException e) {
			response.setLocalError(e);
		}

		return response;
	}
}
