package org.nyer.sns.http.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 拼装URL
 * 
 * @author [[mailto:hzmaoyinjie@corp.netease.com][Mao Yinjie]]
 */
public class UrlBuilder {

    /**
     * initial url
     */
    private String url;

    /**
     * 参数
     */
    private Map<String, String> params = new HashMap<String, String>();

    /**
     * @param url initial url
     */
    public UrlBuilder(String url) {
        this.url = url;
    }

    /**
     * 增加一个参数
     * 
     * @param paramName 参数名称
     * @param paramValue 参数值
     * @return UrlBuilder本身
     */
    public UrlBuilder add(String paramName, Object paramValue) {
        String value = paramValue != null ? paramValue.toString() : "";
        params.put(paramName, value);
        return this;
    }

    /**
     * 增加一组参数
     * 
     * @param params 一组参数
     * @return UrlBuilder本身
     */
    public UrlBuilder add(Map<String, ? extends Object> params) {
        for (Entry<String, ? extends Object> entry : params.entrySet()) {
            add(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * 拼装url
     * 
     * @param charset 对参数进行url encode时使用的编码
     * @return 拼装好的url
     */
    public String toUrl(String charset) {
        try {
            StringBuilder builder = new StringBuilder(url);
            String separator = url.indexOf("?") == -1 ? "?" : "&";
            for (Entry<String, String> entry : params.entrySet()) {
                builder.append(separator).append(URLEncoder.encode(entry.getKey(), charset)).append("=").append(
                    URLEncoder.encode(entry.getValue(), charset));
                separator = "&";
            }
            return builder.toString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
