package org.nyer.sns.http;

import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.HttpStatus;

public class CachedHttpResponse {

    protected int statusCode;

    protected String responseText;

    protected Header[] headers;

    public CachedHttpResponse(int statusCode, String responseText, Header[] headers) {
        this.statusCode = statusCode;
        this.responseText = responseText;
        this.headers = headers;
    }

    public boolean isStatusCodeOK() {
        return this.statusCode == HttpStatus.SC_OK;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseText() {
        return responseText;
    }

    public Header[] getHeaders() {
        return headers;
    }

    public Header[] getHeaders(String name) {
        ArrayList<Header> headersFound = new ArrayList<Header>();

        if (headers != null) {
            for (Header header : headers) {
                if (header.getName().equalsIgnoreCase(name)) {
                    headersFound.add(header);
                }
            }
        }

        return headersFound.toArray(new Header[headersFound.size()]);
    }

	@Override
	public String toString() {
		return "CachedHttpResponse [statusCode=" + statusCode
				+ ", responseText=" + responseText + "]";
	}
}