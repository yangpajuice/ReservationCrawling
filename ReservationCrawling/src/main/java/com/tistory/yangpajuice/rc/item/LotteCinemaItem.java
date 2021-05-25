package com.tistory.yangpajuice.rc.item;

import java.util.*;

public class LotteCinemaItem implements IDataItem {

	private List<LotteCinemaEventItem> Items;
	
	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<LotteCinemaEventItem> getItems() {
		return Items;
	}

	public void setItems(List<LotteCinemaEventItem> items) {
		Items = items;
	}
}
