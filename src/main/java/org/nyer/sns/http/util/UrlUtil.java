package org.nyer.sns.http.util;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class UrlUtil {

    /**
     * 解析形似key1=val1&key2=val2的字符串
     *
     * @param params 形似key1=val1&key2=val2的字符串
     * @return 键值对，以map形式返回
     */
    public static Map<String, String> parseUrlParams(String params) {
        Map<String, String> map = new HashMap<String, String>();
        if (StringUtils.isBlank(params)) {
            return map;
        }

        String[] parts = params.split("&");
        for (String part : parts) {
            if (part.length() > 0) {
                int index = part.indexOf('=');
                if (index != -1 && index < part.length() - 1) {
                    map.put(part.substring(0, index), part.substring(index + 1));
                }
            }
        }
        return map;
    }

    public static Map<String, String> parseParamsFromUri(String uri) {
        try {
            return parseUrlParams(new URI(uri).getQuery());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Unable to parse params from uri: " + uri);
        }
    }

    public static String encodeURIComponent(String uriComponent, String charset) {
        if (uriComponent == null) {
            return "";
        }
        try {
            return URLEncoder.encode(uriComponent, charset);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Unable to encode uriComponent with charset: " + charset, e);
        }
    }

    public static String decodeURIComponent(String uriComponent, String charset) {
        if (uriComponent == null) {
            return "";
        }
        try {
            return URLDecoder.decode(uriComponent, charset);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Unable to decode uriComponent with charset: " + charset, e);
        }
    }
}
