package com.tistory.yangpajuice.rc.item;

import java.lang.reflect.*;

public class CampingItem implements IDataItem {
	private String area = "";
	private String no = "";
	private CampingState state = CampingState.UNKNOWN;
	
	@Override
	public String getKey() {
		return area + "_" + no;
	}
	
	public String toString() {
		String rtnValue = "";
		
		try {
			for (Field field : this.getClass().getDeclaredFields()) {
				String name = field.getName();
				Object value = field.get(this);
				
				// delimiter
				if (rtnValue.length() == 0) {
					
				} else {
					rtnValue += " ";
				}
				
				rtnValue += "[" + name + ":" + value.toString() + "]";
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		
		return rtnValue;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public CampingState getState() {
		return state;
	}

	public void setState(CampingState state) {
		this.state = state;
	}
}
