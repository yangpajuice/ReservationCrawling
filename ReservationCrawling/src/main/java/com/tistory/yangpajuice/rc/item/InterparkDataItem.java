package com.tistory.yangpajuice.rc.item;

import java.util.*;

public class InterparkDataItem implements IDataItem {

	private String playSeq = "";
	private String playDate = "";
	private String playTime = "";
	private String bookableDate = "";
	private String bookingEndDate = "";
	private String cancelableDate = "";
	
	private List<InterparkRemainSeat> remainSeat;		
	
	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPlaySeq() {
		return playSeq;
	}

	public void setPlaySeq(String playSeq) {
		this.playSeq = playSeq;
	}

	public String getPlayDate() {
		return playDate;
	}

	public void setPlayDate(String playDate) {
		this.playDate = playDate;
	}

	public String getPlayTime() {
		return playTime;
	}

	public void setPlayTime(String playTime) {
		this.playTime = playTime;
	}

	public String getBookableDate() {
		return bookableDate;
	}

	public void setBookableDate(String bookableDate) {
		this.bookableDate = bookableDate;
	}

	public String getBookingEndDate() {
		return bookingEndDate;
	}

	public void setBookingEndDate(String bookingEndDate) {
		this.bookingEndDate = bookingEndDate;
	}

	public String getCancelableDate() {
		return cancelableDate;
	}

	public void setCancelableDate(String cancelableDate) {
		this.cancelableDate = cancelableDate;
	}

	public List<InterparkRemainSeat> getRemainSeat() {
		return remainSeat;
	}

	public void setRemainSeat(List<InterparkRemainSeat> remainSeat) {
		this.remainSeat = remainSeat;
	}
}
