package com.tistory.yangpajuice.rc.item;

public class InterparkItem implements IDataItem {

	private InterparkCommonItem common;
	private InterparkDataItem data;
	
	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return null;
	}

	public InterparkCommonItem getCommon() {
		return common;
	}

	public void setCommon(InterparkCommonItem common) {
		this.common = common;
	}

	public InterparkDataItem getData() {
		return data;
	}

	public void setData(InterparkDataItem data) {
		this.data = data;
	}
}
