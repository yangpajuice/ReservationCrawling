package com.tistory.yangpajuice.rc.item;

public class LotteCinemaEventItem implements IDataItem {
	private String EventID = "";
	private String EventName = "";
	private String EventClassificationCode = "";
	private String EventTypeCode = "";
	private String EventTypeName = "";
	private String ProgressStartDate = "";
	private String ProgressEndDate = "";
	private String ImageUrl = "";
	private String ImageAlt = "";
	private String ImageDivisionCode = "";
	private String CinemaID = "";
	private String CinemaName = "";
	private String CinemaAreaCode = "";
	private String CinemaAreaName = "";
	private String DevTemplateYN = "";
	private String CloseNearYN = "";
	private String RemainsDayCount = "";
	private String EventWinnerYN = "";
	private String EventSeq = "";
	private String EventCntnt = "";
	private String EventNtc = "";
	@Override
	public String getKey() {
		return EventID;
	}
	public String getEventID() {
		return EventID;
	}
	public void setEventID(String eventID) {
		EventID = eventID;
	}
	public String getEventName() {
		return EventName;
	}
	public void setEventName(String eventName) {
		EventName = eventName;
	}
	public String getEventClassificationCode() {
		return EventClassificationCode;
	}
	public void setEventClassificationCode(String eventClassificationCode) {
		EventClassificationCode = eventClassificationCode;
	}
	public String getEventTypeCode() {
		return EventTypeCode;
	}
	public void setEventTypeCode(String eventTypeCode) {
		EventTypeCode = eventTypeCode;
	}
	public String getEventTypeName() {
		return EventTypeName;
	}
	public void setEventTypeName(String eventTypeName) {
		EventTypeName = eventTypeName;
	}
	public String getProgressStartDate() {
		return ProgressStartDate;
	}
	public void setProgressStartDate(String progressStartDate) {
		ProgressStartDate = progressStartDate;
	}
	public String getProgressEndDate() {
		return ProgressEndDate;
	}
	public void setProgressEndDate(String progressEndDate) {
		ProgressEndDate = progressEndDate;
	}
	public String getImageUrl() {
		return ImageUrl;
	}
	public void setImageUrl(String imageUrl) {
		ImageUrl = imageUrl;
	}
	public String getImageAlt() {
		return ImageAlt;
	}
	public void setImageAlt(String imageAlt) {
		ImageAlt = imageAlt;
	}
	public String getImageDivisionCode() {
		return ImageDivisionCode;
	}
	public void setImageDivisionCode(String imageDivisionCode) {
		ImageDivisionCode = imageDivisionCode;
	}
	public String getCinemaID() {
		return CinemaID;
	}
	public void setCinemaID(String cinemaID) {
		CinemaID = cinemaID;
	}
	public String getCinemaName() {
		return CinemaName;
	}
	public void setCinemaName(String cinemaName) {
		CinemaName = cinemaName;
	}
	public String getCinemaAreaCode() {
		return CinemaAreaCode;
	}
	public void setCinemaAreaCode(String cinemaAreaCode) {
		CinemaAreaCode = cinemaAreaCode;
	}
	public String getCinemaAreaName() {
		return CinemaAreaName;
	}
	public void setCinemaAreaName(String cinemaAreaName) {
		CinemaAreaName = cinemaAreaName;
	}
	public String getDevTemplateYN() {
		return DevTemplateYN;
	}
	public void setDevTemplateYN(String devTemplateYN) {
		DevTemplateYN = devTemplateYN;
	}
	public String getCloseNearYN() {
		return CloseNearYN;
	}
	public void setCloseNearYN(String closeNearYN) {
		CloseNearYN = closeNearYN;
	}
	public String getRemainsDayCount() {
		return RemainsDayCount;
	}
	public void setRemainsDayCount(String remainsDayCount) {
		RemainsDayCount = remainsDayCount;
	}
	public String getEventWinnerYN() {
		return EventWinnerYN;
	}
	public void setEventWinnerYN(String eventWinnerYN) {
		EventWinnerYN = eventWinnerYN;
	}
	public String getEventSeq() {
		return EventSeq;
	}
	public void setEventSeq(String eventSeq) {
		EventSeq = eventSeq;
	}
	public String getEventCntnt() {
		return EventCntnt;
	}
	public void setEventCntnt(String eventCntnt) {
		EventCntnt = eventCntnt;
	}
	public String getEventNtc() {
		return EventNtc;
	}
	public void setEventNtc(String eventNtc) {
		EventNtc = eventNtc;
	}
}
