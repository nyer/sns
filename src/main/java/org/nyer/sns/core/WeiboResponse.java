package org.nyer.sns.core;


/**
 * 微博服务请求的响应
 * @author leiting
 *
 */
public class WeiboResponse {
	public static WeiboResponse NO_TOKEN_RESPONSE;
	static {
		NO_TOKEN_RESPONSE = new WeiboResponse();
		NO_TOKEN_RESPONSE.setStatus(WeiboProtocal.AUTH_ERROR);
	}
	
	private int status;
	private boolean localError;
	private Exception exp;
	private int httpStatus;
	private String httpResponseText;
	
	public WeiboResponse() {
		this.status = WeiboProtocal.SUCCESS;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public boolean isLocalError() {
		return localError;
	}

	public void setLocalError(Exception exp) {
		this.status = WeiboProtocal.OTHER_ERROR;
		
		this.localError = true;
		this.exp = exp;
	}
	
	public Exception getException() {
		return exp;
	}

	public int getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(int httpStatus) {
		this.httpStatus = httpStatus;
	}
	
	public boolean isStatusOK() {
		return this.status == WeiboProtocal.SUCCESS;
	}

	public String getHttpResponseText() {
		return httpResponseText;
	}

	public void setHttpResponseText(String httpResponseText) {
		this.httpResponseText = httpResponseText;
	}

	@Override
	public String toString() {
		return "WeiboResponse [status=" + status + ", localError=" + localError
				+ ", exp=" + exp + ", httpStatus=" + httpStatus
				+ ", httpResponseText=" + httpResponseText + "]";
	}
}
