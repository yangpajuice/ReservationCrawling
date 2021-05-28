package com.tistory.yangpajuice.rc.item.event;

import com.tistory.yangpajuice.rc.item.*;

public class CgvAddedEvent {
	private WebPageItem webPageItem;
	
	public CgvAddedEvent(WebPageItem webPageItem) {
		this.webPageItem = webPageItem;
	}

	public WebPageItem getWebPageItem() {
		return webPageItem;
	}

	public void setWebPageItem(WebPageItem webPageItem) {
		this.webPageItem = webPageItem;
	}
}
