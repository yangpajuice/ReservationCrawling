package com.tistory.yangpajuice.rc.item;

import java.util.*;

public class LotteCinemaEventResponse {
	private List<LotteCinemaEventItem> Items;
	
	private int TotalCount = 0;
	private String IsOK = "";
	private String ResultMessage = "";
	private String ResultCode = "";
	private String EventResultYn = "";
	
	public List<LotteCinemaEventItem> getItems() {
		return Items;
	}
	
	public void setItems(List<LotteCinemaEventItem> items) {
		Items = items;
	}
	
	public int getTotalCount() {
		return TotalCount;
	}
	
	public void setTotalCount(int totalCount) {
		TotalCount = totalCount;
	}
	
	public String getIsOK() {
		return IsOK;
	}
	
	public void setIsOK(String isOK) {
		IsOK = isOK;
	}
	
	public String getResultMessage() {
		return ResultMessage;
	}
	
	public void setResultMessage(String resultMessage) {
		ResultMessage = resultMessage;
	}
	
	public String getResultCode() {
		return ResultCode;
	}
	
	public void setResultCode(String resultCode) {
		ResultCode = resultCode;
	}
	
	public String getEventResultYn() {
		return EventResultYn;
	}
	
	public void setEventResultYn(String eventResultYn) {
		EventResultYn = eventResultYn;
	}
}
