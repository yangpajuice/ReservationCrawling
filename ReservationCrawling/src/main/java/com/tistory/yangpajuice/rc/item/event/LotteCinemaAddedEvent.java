package com.tistory.yangpajuice.rc.item.event;

import com.tistory.yangpajuice.rc.item.*;

public class LotteCinemaAddedEvent {
	private WebPageItem webPageItem = null;
	
	public LotteCinemaAddedEvent(WebPageItem webPageItem) {
		this.webPageItem = webPageItem;
	}

	public WebPageItem getWebPageItem() {
		return webPageItem;
	}
}
