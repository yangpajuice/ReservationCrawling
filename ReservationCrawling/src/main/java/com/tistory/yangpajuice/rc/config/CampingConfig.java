package com.tistory.yangpajuice.rc.config;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

@Component
public class CampingConfig {
	@Value("${camping.reservation.datelist}")
	private String reservationDateList = ""; // ex) 20200424,20200425

	public String getReservationDateList() {
		return reservationDateList;
	}

	public void setReservationDateList(String reservationDateList) {
		this.reservationDateList = reservationDateList;
	}
}
