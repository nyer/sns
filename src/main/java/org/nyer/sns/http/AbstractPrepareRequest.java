package org.nyer.sns.http;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.nyer.sns.http.util.HttpUtil;
import org.nyer.sns.http.util.UrlBuilder;

public abstract class AbstractPrepareRequest implements PrepareRequest {
    
    protected HttpClient httpClient;
    private String url;
    private String charset = "UTF-8";
    private List<Header> headers = new ArrayList<Header>();
    private List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    private BasicHttpParams httpParams = new BasicHttpParams();
    private HttpEntity httpEntity;
    
    public AbstractPrepareRequest(HttpClient httpClient, String url) {
        this.httpClient = httpClient;
        this.url = url;
    }
    
    public PrepareRequest charset(String charset) {
        this.charset = charset;
        return this;
    }
    
    @Override
    public String getCharset() {
        return this.charset;
    }
    
    @Override
    public PrepareRequest header(Header header) {
        this.headers.add(header);
        return this;
    }
    
    @Override
    public PrepareRequest headers(List<Header> headers) {
        for (int i = 0, s = headers.size();i < s; i ++) {
            this.headers.add(headers.get(i));
        }
        
        return this;
    }

    @Override
    public PrepareRequest header(String name, String value) {
        Header header = new BasicHeader(name, value);
        return header(header);
    }

    @Override
    public PrepareRequest parameter(NameValuePair pair) {
        parameters.add(pair);
        return this;
    }
    
    @Override
    public PrepareRequest parameters(List<NameValuePair> pairs) {
        for (int i = 0, s = pairs.size();i < s; i++) {
            this.parameters.add(pairs.get(i));
        }
        
        return this;
    }
    
    @Override
    public PrepareRequest parameter(String name, String value) {
        NameValuePair pair = new BasicNameValuePair(name, value);
        return parameter(pair);
    }

    @Override
    public PrepareRequest httpParameter(String name, Object value) {
        httpParams.setParameter(name, value);
        return this;
    }

    @Override
    public PrepareRequest httpParams(BasicHttpParams params) {
    	Set<String> paramNames = params.getNames();
    	for (String key : paramNames) {
    		httpParams.setParameter(key, params.getParameter(key));
    	}
    	return this;
    }
    
    @Override
    public PrepareRequest requestEntity(String entity) {
        try {
            this.httpEntity = new StringEntity(entity, charset);
        } catch (UnsupportedEncodingException e) {
            // keep silient
        }
        
        return requestEntity(entity);
    }

    @Override
    public PrepareRequest requestEntity(HttpEntity entity) {
        this.httpEntity = entity;
        return this;
    }
    
    @Override
    public List<Header> getHeaders() {
        return headers;
    }

    @Override
    public List<NameValuePair> getNameValuePair() {
        return parameters;
    }

    @Override
    public BasicHttpParams getHttpParams() {
        return httpParams;
    }

    @Override
    public HttpEntity getRequestEntity() {
        return httpEntity;
    }
    
    @Override
    public String url() {
        return url;
    }
    
    /**
     * 设置请求头部
     * 
     * @param httpMethod 需要设置头部的HttpMethod对象
     * @param headers 头部，可以为null
     */
    public void addRequestHeaders(HttpUriRequest request, List<Header> headers) {
        if (headers != null) {
            for (Header header : headers) {
                request.addHeader(header);
            }
        }
    }

    public void copyHttpParams(HttpUriRequest request, BasicHttpParams params) {
        HttpParams reqParams = request.getParams();
        if (params != null) {
            for (String name : params.getNames()) {
                Object value = params.getParameter(name);
                reqParams.setParameter(name, value);
            }
        }
    }

    public HttpPost createHttpPost() {
        if (this.httpEntity == null && this.parameters.size() > 0 ) {
            this.httpEntity = HttpUtil.createUrlEncodedFormEntity(parameters, charset);
        }

        HttpPost httpPost = new HttpPost(url);
        addRequestHeaders(httpPost, headers);
        copyHttpParams(httpPost, httpParams);
        
        httpPost.setEntity(httpEntity);
        
        return httpPost;
    }

    public HttpGet createHttpGet() {
        UrlBuilder urlBuilder = new UrlBuilder(url);
        for (int i =0, s = parameters.size();i < s; i ++) {
            NameValuePair param = parameters.get(i);
            urlBuilder.add(param.getName(), param.getValue());
        }
        
        HttpGet get = new HttpGet(urlBuilder.toUrl(charset));
        addRequestHeaders(get, headers);
        copyHttpParams(get, httpParams);
        
        return get;
    }
    
    @Override
    public String ensureOK() throws Exception {
        CachedHttpResponse response = send();
        if (response.isStatusCodeOK() == false) {
            throw new Exception("request failed, url: " + url + ", code:" + response.getStatusCode() + ", text: " + response.getResponseText());
        }
        
        return response.getResponseText();
    }
}
