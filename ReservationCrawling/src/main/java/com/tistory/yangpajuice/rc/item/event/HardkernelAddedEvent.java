package com.tistory.yangpajuice.rc.item.event;

import com.tistory.yangpajuice.rc.item.*;

public class HardkernelAddedEvent {
	private WebPageItem webPageItem;
	
	public HardkernelAddedEvent(WebPageItem webPageItem) {
		this.webPageItem = webPageItem;
	}

	public WebPageItem getWebPageItem() {
		return webPageItem;
	}

	public void setWebPageItem(WebPageItem webPageItem) {
		this.webPageItem = webPageItem;
	}
}
