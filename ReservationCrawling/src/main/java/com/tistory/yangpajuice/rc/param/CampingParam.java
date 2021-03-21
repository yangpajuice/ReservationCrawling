package com.tistory.yangpajuice.rc.param;

public class CampingParam implements IDataParam {
	private String site = "";
	private String reservatinDate = "";
	
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	public String getReservatinDate() {
		return reservatinDate;
	}
	public void setReservatinDate(String reservatinDate) {
		this.reservatinDate = reservatinDate;
	}
}
