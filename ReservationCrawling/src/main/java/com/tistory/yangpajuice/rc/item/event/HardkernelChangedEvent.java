package com.tistory.yangpajuice.rc.item.event;

import com.tistory.yangpajuice.rc.item.*;

public class HardkernelChangedEvent {
	private WebPageItem webPageItemFromWeb;
	private WebPageItem webPageItemFromDb;
	
	public HardkernelChangedEvent(WebPageItem webPageItemFromWeb, WebPageItem webPageItemFromDb) {
		this.webPageItemFromWeb = webPageItemFromWeb;
		this.webPageItemFromDb = webPageItemFromDb;
	}

	public WebPageItem getWebPageItemFromWeb() {
		return webPageItemFromWeb;
	}

	public WebPageItem getWebPageItemFromDb() {
		return webPageItemFromDb;
	}
}
