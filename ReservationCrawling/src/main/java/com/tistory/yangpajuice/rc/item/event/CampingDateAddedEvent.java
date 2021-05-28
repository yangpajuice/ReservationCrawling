package com.tistory.yangpajuice.rc.item.event;

public class CampingDateAddedEvent {
	private String siteName = "";
	private String dateDesc = "";
	
	public CampingDateAddedEvent(String siteName, String dateDesc) {
		this.siteName = siteName;
		this.dateDesc = dateDesc;
	}

	public String getSiteName() {
		return siteName;
	}

	public String getDateDesc() {
		return dateDesc;
	}
}
