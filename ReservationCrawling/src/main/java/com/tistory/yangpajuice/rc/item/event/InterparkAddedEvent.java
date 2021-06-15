package com.tistory.yangpajuice.rc.item.event;

import com.tistory.yangpajuice.rc.item.*;

public class InterparkAddedEvent {
	private String link = "";
	private InterparkItem interparkItem = null;
	
	public InterparkAddedEvent(String link, InterparkItem interparkItem) {
		this.link = link;
		this.interparkItem = interparkItem;
	}
	
	public String getLink() {
		return link;
	}

	public InterparkItem getInterparkItem() {
		return interparkItem;
	}
}
