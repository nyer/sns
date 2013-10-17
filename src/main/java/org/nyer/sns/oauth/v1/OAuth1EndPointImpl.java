package org.nyer.sns.oauth.v1;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.log4j.Logger;
import org.nyer.sns.http.PrepareHttpClient;
import org.nyer.sns.http.PrepareRequest;
import org.nyer.sns.oauth.OAuthConstants;
import org.nyer.sns.oauth.OAuthDeveloperAccount;
import org.nyer.sns.token.OAuthTokenPair;

public class OAuth1EndPointImpl implements OAuth1EndPoint, OAuthConstants {
    private static final Logger log = Logger.getLogger(LOG_NAME);
    
	/** OAuth 1.0服务一般使用的加密算法（在调用Mac.getInstance时传入这个值） */
    public static final String HMAC_SHA1 = "HmacSHA1";

    /** OAuth 1.0服务一般使用的加密算法（在调用API时传入这个值） */
    public static final String HMAC_SHA1_NAME = "HMAC-SHA1";

    /**
     * A Random object should be thread safe after JDK 1.4.2. See
     * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6362070 for details.
     */
    private static final Random random = new Random();
    
    private PrepareHttpClient httpClient;
    
	private OAuth1 oauth;
    
	public OAuth1EndPointImpl(OAuth1 oauth, PrepareHttpClient httpClient) {
		this.oauth = oauth;
		this.httpClient = httpClient;
	}
	
	@Override
	public PrepareRequest post(String baseUri,
			Map<String, String> additionalParams, HttpEntity requestEntity,
			OAuthTokenPair accessToken) {
		Map<String, String> params = generateParams(true, baseUri, additionalParams, oauth.getDeveloperAccount(), accessToken);

		PrepareRequest req = httpClient.preparePost(baseUri);
		req.httpParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, "UTF-8");
		
		for (Entry<String, String> entry : params.entrySet()) {
            req.parameter(entry.getKey(), entry.getValue());
        }
		
		if (requestEntity != null)
			req.requestEntity(requestEntity);
		
		return req;
	}
	
	@Override
	public PrepareRequest post(String baseUri,
			Map<String, String> additionalParams, OAuthTokenPair tokenPair) {
		return post(baseUri, additionalParams, null, tokenPair);
	}

	@Override
	public PrepareRequest post(String baseUri, HttpEntity requestEntity,
			OAuthTokenPair tokenPair) {
		return post(baseUri, null, requestEntity, tokenPair);
	}

	@Override
	public PrepareRequest post(String baseUri, OAuthTokenPair tokenPair) {
		return post(baseUri, (Map<String, String>)null, tokenPair);
	}

	@Override
	public PrepareRequest get(String baseUri,
			Map<String, String> additionalParams, OAuthTokenPair tokenPair) {
		Map<String, String> params = generateParams(false, baseUri, additionalParams, oauth.getDeveloperAccount(), tokenPair);

		PrepareRequest req = httpClient.prepareGet(baseUri);
		for (Entry<String, String> entry : params.entrySet()) {
            req.parameter(entry.getKey(), entry.getValue());
        }
		
		return req;
	}

	@Override
	public PrepareRequest get(String baseUri,
			Map<String, String> additionalParams) {
		return get(baseUri, additionalParams, null);
	}
	
	@Override
	public PrepareRequest get(String baseUri, OAuthTokenPair tokenPair) {
		return get(baseUri, null, tokenPair);
	}

	/**
     * 生成调用API接口的参数
     * 
     * @param post 是否post请求。只允许两种请求类型：POST或GET
     * @param baseUri API接口URL，不包含参数
     * @param additionalParams 这个方法会自动设置OAuth 1.0需要的一些参数。如果调用者还需要传入其它参数，通过additionalParams传递。
     * @param developerAccount 开发者账号
     * @param tokenPair 接口不同，这个参数的意义也不一样。允许的值是request token and secret、access token and secret或null。
     */
    private static Map<String, String> generateParams(boolean post, String baseUri,
                                                      Map<String, String> additionalParams,
                                                      OAuthDeveloperAccount developerAccount, OAuthTokenPair tokenPair) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("oauth_consumer_key", developerAccount.getKey()); // Consumer Key
        params.put("oauth_signature_method", HMAC_SHA1_NAME); // 签名方法，暂时只支持HMAC-SHA1
        params.put("oauth_timestamp", generateTimestamp()); // 时间戳, 其值是距1970 00:00:00 GMT的秒数，必须是大于0的整数
        params.put("oauth_nonce", generateNonce()); // 单次值，随机生成的32位字符串，防止重放攻击（每次请求必须不同）
        params.put("oauth_version", "1.0"); // (可选)版本号，目前所有OAuth 1.0使用版本号1.0
        if (tokenPair != null) {
            params.put("oauth_token", tokenPair.getToken());
        }
        if (additionalParams != null) {
            params.putAll(additionalParams);
        }

        String httpMethod = post ? "POST" : "GET";
        String signatureSource = generateSignatureSource(httpMethod, baseUri, params);
        String signature = generateSignature(signatureSource, developerAccount, tokenPair);
        params.put("oauth_signature", signature); // 签名值，密钥为：Consumer Secret&Token Secret。

        return params;
    }

    /**
     * 生成用于签名的文本
     * 
     * @param httpMethod http请求的method（GET或POST）
     * @param baseUri 接口url，不包含参数
     * @param params 参数
     * @return 用于签名的文本
     */
    private static String generateSignatureSource(String httpMethod, String baseUri, Map<String, String> params) {
        StringBuilder signatureSource = new StringBuilder(httpMethod + "&" + normalizeParam4Signature(baseUri) + "&");
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        boolean first = true;
        for (String key : keys) {
            if (!first) {
                signatureSource.append("%26");
            }
            String value = params.get(key);
            value = normalizeParam4Signature(normalizeParam4Signature(value));
            signatureSource.append(normalizeParam4Signature(key)).append("%3D").append(value);
            first = false;
        }
        return signatureSource.toString();
    }

    /**
     * 生成一段文本的签名
     * 
     * @param text 需要生成签名的文本
     * @param developerAccount 开发者账号
     * @param tokenPair token & secret，视接口要求，可能是request token & secret或access token & secret。
     * @return 签名
     */
    private static String generateSignature(String text, OAuthDeveloperAccount developerAccount,
                                            OAuthTokenPair tokenPair) {
        try {
            String oauthSignature = normalizeParam4Signature(developerAccount.getSecret()) + "&";
            if (tokenPair != null && StringUtils.isNotBlank(tokenPair.getTokenSecret())) {
                oauthSignature += normalizeParam4Signature(tokenPair.getTokenSecret());
            }
            SecretKeySpec spec = new SecretKeySpec(oauthSignature.getBytes(), HMAC_SHA1);
            Mac mac = Mac.getInstance(HMAC_SHA1);
            mac.init(spec);
            byte[] byteHMAC = mac.doFinal(text.getBytes());
            return encodeBase64(byteHMAC);
        } catch (InvalidKeyException e) {
            // ignore
            log.error("", e);
        } catch (NoSuchAlgorithmException e) {
            // ignore
            log.error("", e);
        }
        return null;
    }
    
    /**
     * 生成调用API接口需要的Authorization header
     * 
     * @param params 调用API接口的所有参数
     * @param authHeaderRealm Authorization header中realm的值
     * @return 调用API接口需要的Authorization header
     */
    @SuppressWarnings("unused")
	private static String generateAuthorizationHeader(Map<String, String> params, String authHeaderRealm) {
        StringBuilder authorizationHeader = new StringBuilder();
        for (Entry<String, String> entry : params.entrySet()) {
            if (authorizationHeader.length() != 0) {
                authorizationHeader.append(",");
            }
            authorizationHeader.append(normalizeParam4Signature(entry.getKey()));
            authorizationHeader.append("=\"").append(normalizeParam4Signature(entry.getValue())).append("\"");
        }

        return "OAuth " + (authHeaderRealm != null ? ("realm=\"" + authHeaderRealm + "\",") : "") +
            authorizationHeader.toString();
    }

    /**
     * <pre>
     * 签名的各个组成部分，必须首先使用这个方法进行“转义”或者“规范化”
     * 过程是：
     * 1. 首先使用UTF-8对它进行url encode
     * 2. 把结果中的*替换为%2A，把+替换为%20，把%7E替换为~
     * </pre>
     * 
     * @param value 需要“规范化”的字符串
     * @return “规范化”后的字符串
     */
    private static String normalizeParam4Signature(String value) {
        String encoded = null;
        try {
            encoded = URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("", e);
            return null;
        }
        StringBuffer buf = new StringBuffer(encoded.length());
        char focus;
        for (int i = 0; i < encoded.length(); i++) {
            focus = encoded.charAt(i);
            if (focus == '*') {
                buf.append("%2A");
            } else if (focus == '+') {
                buf.append("%20");
            } else if (focus == '%' && (i + 1) < encoded.length() && encoded.charAt(i + 1) == '7' &&
                encoded.charAt(i + 2) == 'E') {
                buf.append('~');
                i += 2;
            } else {
                buf.append(focus);
            }
        }
        return buf.toString();
    }

    /**
     * @return 生成一个随机数，尽量避免重复
     */
    private static String generateNonce() {
        long timestamp = System.currentTimeMillis() / 1000;
        return String.valueOf(timestamp + random.nextInt());
    }

    /**
     * @return 当前时间，单位为秒
     */
    private static String generateTimestamp() {
        long timestamp = System.currentTimeMillis() / 1000;
        return String.valueOf(timestamp);
    }

    /**
     * 对一个字节数组进行Base64编码
     * 
     * @param bytes 需要编码的字节数组
     * @return 编码后的字符串
     */
    private static String encodeBase64(byte[] bytes) {
        try {
            return new String(Base64.encodeBase64(bytes), "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            // ignore
            log.error("", e);
            return null;
        }
    }
}
