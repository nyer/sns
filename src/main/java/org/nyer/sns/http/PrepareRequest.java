package org.nyer.sns.http;

import java.io.IOException;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.params.BasicHttpParams;

public interface PrepareRequest {
    PrepareRequest charset(String charset);
    
    PrepareRequest header(Header header);
    
    PrepareRequest headers(List<Header> headers);
    
    PrepareRequest header(String name, String value);
    
    PrepareRequest parameter(NameValuePair pair);
    
    PrepareRequest parameters(List<NameValuePair> pairs);
    
    PrepareRequest parameter(String name, String value);

    PrepareRequest httpParams(BasicHttpParams params);
    
    PrepareRequest httpParameter(String name, Object value);
    
    PrepareRequest requestEntity(String entity);
    
    PrepareRequest requestEntity(HttpEntity entity);
    
    String getCharset();
    
    List<Header> getHeaders();
    
    List<NameValuePair> getNameValuePair();
    
    BasicHttpParams getHttpParams();
    
    HttpEntity getRequestEntity();
    
    CachedHttpResponse send() throws IOException;
    
    String ensureOK() throws Exception;
    
    String url();
}
