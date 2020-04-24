package com.tistory.yangpajuice.rc.util;

import java.text.*;
import java.util.*;

import com.tistory.yangpajuice.rc.service.*;

public class StrUtil {
	public static String toDateFormat(String fromFormat, String toFormat, String data) {
		String rtnValue = "";
		
		try {
			SimpleDateFormat transFormat = new SimpleDateFormat(fromFormat, Locale.KOREAN);
			Date oTime = transFormat.parse(data);
			rtnValue = new SimpleDateFormat(toFormat).format(oTime);
			
		} catch (Exception e) {
			
		}
		
		return rtnValue;
	}
	
	public static String toDateFormat(String format, Calendar cal) {
		String rtnValue = "";
		
		try {
			SimpleDateFormat transFormat = new SimpleDateFormat(format, Locale.KOREAN);
			rtnValue = transFormat.format(cal.getTime());
			
		} catch (Exception e) {
			
		}
		
		return rtnValue;
	}
	
	public static Calendar toCalendarFormat(String format, String dateString) {
		Calendar cal = null;
		
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(format);
			Date date = formatter.parse(dateString);
			cal = Calendar.getInstance();
			cal.setTime(date);
			
		} catch (Exception e) {
			
		}
		
		return cal;
	}
}
