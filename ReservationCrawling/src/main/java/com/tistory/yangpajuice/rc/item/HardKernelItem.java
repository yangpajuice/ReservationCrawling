package com.tistory.yangpajuice.rc.item;

public class HardKernelItem implements IDataItem {
	private String name;
	private String price;

	@Override
	public String getKey() {
		return name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}
}
