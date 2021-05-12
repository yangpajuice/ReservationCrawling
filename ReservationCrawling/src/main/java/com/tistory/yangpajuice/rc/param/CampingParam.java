package com.tistory.yangpajuice.rc.param;

public class CampingParam implements IDataParam {
	private String site = "";
	private String reservatinDate = "";
	private int seq = 0;
	private String area = "";
	private String no = "";
	
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
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
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
}
