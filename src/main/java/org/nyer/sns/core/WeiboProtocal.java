package org.nyer.sns.core;

import java.util.Map;

import org.apache.http.HttpEntity;
import org.nyer.sns.http.PrepareRequest;
import org.nyer.sns.oauth.OAuthEndPoint;
import org.nyer.sns.token.OAuthTokenPair;

/**
 * 微博服务的协议
 * @author leiting
 *
 */
public interface WeiboProtocal {
	/** 成功 **/
    int SUCCESS = 0;

    /** 重复发表 */
    int REPEAT_ERROR = 1;

    /** 连接错误 */
    int CONNECTION_ERROR = 2;

    /** 频率过快 */
    int FREQUENCY_ERROR = 3;

    /** 敏感字符 */
    int BAN_WORD_ERROR = 4;

    /** 认证错误 */
    int AUTH_ERROR = 5;

    /** 其他未知 */
    int  OTHER_ERROR = 6;
    
    /**
     * 错误处理
     * @param request
     * @return
     */
    WeiboResponse process(PrepareRequest request);
    
    WeiboResponse post(String baseUri, Map<String, String> additionalParams, HttpEntity requestEntity, OAuthTokenPair accessToken);
    WeiboResponse post(String baseUri, Map<String, String> additionalParams, OAuthTokenPair accessToken);
    WeiboResponse post(String baseUri, HttpEntity requestEntity, OAuthTokenPair accessToken);
    WeiboResponse post(String baseUri, OAuthTokenPair accessToken);
	
    WeiboResponse get(String baseUri, Map<String, String> additionalParams,OAuthTokenPair accessToken);
    WeiboResponse get(String baseUri, Map<String, String> additionalParams) ;
    WeiboResponse get(String baseUri, OAuthTokenPair accessToken) ;
    
    OAuthEndPoint getEndPoint();
    
    void setEndPoint(OAuthEndPoint endPoint);
}
