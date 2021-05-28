package com.tistory.yangpajuice.rc.item.event;

import com.tistory.yangpajuice.rc.item.*;

public class PpomppuAddedEvent {
	private WebPageItem item = null;
	
	public PpomppuAddedEvent(WebPageItem item) {
		this.item = item;
	}

	public WebPageItem getItem() {
		return item;
	}
}
