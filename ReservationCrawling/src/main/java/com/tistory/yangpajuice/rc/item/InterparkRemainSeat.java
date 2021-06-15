package com.tistory.yangpajuice.rc.item;

public class InterparkRemainSeat implements IDataItem {

	private String seatGrade = "";
	private String seatGradeName = "";
	private String remainCnt = "";
	
	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSeatGrade() {
		return seatGrade;
	}

	public void setSeatGrade(String seatGrade) {
		this.seatGrade = seatGrade;
	}

	public String getSeatGradeName() {
		return seatGradeName;
	}

	public void setSeatGradeName(String seatGradeName) {
		this.seatGradeName = seatGradeName;
	}

	public String getRemainCnt() {
		return remainCnt;
	}

	public void setRemainCnt(String remainCnt) {
		this.remainCnt = remainCnt;
	}
}
