package com.tistory.yangpajuice.rc.item.event;

import com.tistory.yangpajuice.rc.item.*;

public class ClienAddedEvent {
private WebPageItem item = null;
	
	public ClienAddedEvent(WebPageItem item) {
		this.item = item;
	}

	public WebPageItem getItem() {
		return item;
	}
}
