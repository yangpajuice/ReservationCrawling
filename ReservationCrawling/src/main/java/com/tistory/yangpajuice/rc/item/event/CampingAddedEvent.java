package com.tistory.yangpajuice.rc.item.event;

import com.tistory.yangpajuice.rc.item.*;

public class CampingAddedEvent {
	private String siteName = "";
	private String url = "";
	private String dateDesc = "";
	private CampingItem campingItem = null;
	
	public CampingAddedEvent(String siteName, String url, String dateDesc, CampingItem campingItem) {
		this.siteName = siteName;
		this.url = url;
		this.dateDesc = dateDesc;
		this.campingItem = campingItem;
	}

	public String getSiteName() {
		return siteName;
	}

	public String getUrl() {
		return url;
	}

	public String getDateDesc() {
		return dateDesc;
	}

	public CampingItem getCampingItem() {
		return campingItem;
	}
}
