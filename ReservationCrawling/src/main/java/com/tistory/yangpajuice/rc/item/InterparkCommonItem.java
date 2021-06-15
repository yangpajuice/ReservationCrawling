package com.tistory.yangpajuice.rc.item;

public class InterparkCommonItem implements IDataItem {

	private String messageId = "";
	private String message = "";
	private String requestUri = "";
	private String gtid = "";
	private String timestamp = "";
	private String internalHttpStatusCode = "";
	
	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getRequestUri() {
		return requestUri;
	}

	public void setRequestUri(String requestUri) {
		this.requestUri = requestUri;
	}

	public String getGtid() {
		return gtid;
	}

	public void setGtid(String gtid) {
		this.gtid = gtid;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getInternalHttpStatusCode() {
		return internalHttpStatusCode;
	}

	public void setInternalHttpStatusCode(String internalHttpStatusCode) {
		this.internalHttpStatusCode = internalHttpStatusCode;
	}
}
