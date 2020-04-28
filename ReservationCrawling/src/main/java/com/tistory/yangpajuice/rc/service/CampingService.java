package com.tistory.yangpajuice.rc.service;

import java.util.*;

import javax.annotation.*;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;

import com.tistory.yangpajuice.rc.config.*;
import com.tistory.yangpajuice.rc.item.*;
import com.tistory.yangpajuice.rc.util.*;

public abstract class CampingService implements IService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36";
	private final String DATE_FORMAT = "yyyyMMdd";
	private final int MAX_DAYS = 31;
	private Map<String, Map<String, CampingItem>> cacheCampingItemDateMap = null;
	
	@Autowired
	protected Telegram telegram;
	
	@Autowired
	private CampingConfig campingConfig;
	
	protected abstract String getDefaultUrl();
	protected abstract String getSiteName();
	protected abstract Map<String, String> getInquiryData(Calendar cal);
	protected abstract Map<String, CampingItem> getCampingItemMap(Document doc) throws Exception;
	
	@PostConstruct
    private void init() {
		
		try {
			cacheCampingItemDateMap = getCampingItemDateMap();
			
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
		logger.info("Initialized");
	}
	
	private void removeOldCacheData() throws Exception {
		// remove old data
		Calendar yesterdayCal = Calendar.getInstance();
		yesterdayCal.add(Calendar.DAY_OF_YEAR, -1);
		String parsedDate = StrUtil.toDateFormat(DATE_FORMAT, yesterdayCal);
		if (cacheCampingItemDateMap.remove(parsedDate) != null) {
			logger.info("removed date = " + parsedDate);
		}
	}
	
	@Override
	public void start() {
		logger.info("START");
		
		try {
			removeOldCacheData();
			
			Map<String, Map<String, CampingItem>> campingItemDateMap = getCampingItemDateMap();
			for (String key : campingItemDateMap.keySet()) {
				String dateDesc = StrUtil.toDateFormat(DATE_FORMAT, "yyyy-MM-dd EEE", key);
				Map<String, CampingItem> campingItemMap = campingItemDateMap.get(key);
				Map<String, CampingItem> cacheCampingItemMap = cacheCampingItemDateMap.get(key);
				
				if (cacheCampingItemMap == null) {
					cacheCampingItemDateMap.put(key, campingItemMap);
					logger.info("new date = " + key);
					telegram.sendMessage("*** " + getSiteName() + " Open Date : " + dateDesc);
					
				} else {
					for (String itemKey : campingItemMap.keySet()) {
						CampingItem campingItem = campingItemMap.get(itemKey);
						CampingItem cacheCampingItem = cacheCampingItemMap.get(itemKey);
						
						// replace data
						cacheCampingItemMap.remove(itemKey);
						cacheCampingItemMap.put(itemKey, campingItem);
						
						// check data
						if (cacheCampingItem.getState().equals(CampingState.COMPLETED) == true || 
								cacheCampingItem.getState().equals(CampingState.WAITING) == true) {
							// completed/waiting ==> available
							if (campingItem.getState().equals(CampingState.AVAILABLE) == true) {
								logger.info("[" + key + "] available = " + campingItem.toString());
								telegram.sendMessage("*** " + getSiteName() + " available : " + dateDesc + " " + campingItem.toString());
							}
						}
					}
				}
			}
			
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
		logger.info("END");
	}
	
	private Map<String, Map<String, CampingItem>> getCampingItemDateMap() throws Exception {
		Map<String, Map<String, CampingItem>> campingItemDateMap = new HashMap<String, Map<String,CampingItem>>();
		
		List<Calendar> dateList = new ArrayList<Calendar>();
		String reservationDateList = campingConfig.getReservationDateList();
		if (reservationDateList == null || reservationDateList.length() == 0) { // 설정이 없는 경우 31일 조회
			int days = 0;
			while (days < MAX_DAYS) {
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DAY_OF_YEAR, days);
				dateList.add(cal);
				
				days++;
			}
		} else { // 설정이 있는 경우 설정일만 조회
			String[] dateStringList = reservationDateList.split(",");
			for (String dateString : dateStringList) {
				Calendar cal = StrUtil.toCalendarFormat(DATE_FORMAT, dateString);
				dateList.add(cal);
			}
		}

		for (Calendar cal : dateList) {
			Map<String, CampingItem> campingItemMap = getCampingItemMap(cal);
			
			boolean nonState = false;
			for (String key : campingItemMap.keySet()) {
				CampingItem campingItem = campingItemMap.get(key);
				if (campingItem.getState().equals(CampingState.NONE) == true) {
					nonState = true;
				}
			}
			if (nonState == true) {
				logger.debug("not open = " + StrUtil.toDateFormat(DATE_FORMAT, cal));
				break;
			}

			String parsedDate = StrUtil.toDateFormat(DATE_FORMAT, cal);
			campingItemDateMap.putIfAbsent(parsedDate, campingItemMap);

			Thread.sleep(1000);
		}
		
		return campingItemDateMap;
	}

	private Map<String, CampingItem> getCampingItemMap(Calendar cal) throws Exception {
		logger.debug("inquiry date = " + StrUtil.toDateFormat(DATE_FORMAT, cal));
		Map<String, String> data = getInquiryData(cal);
		
		Document doc = Jsoup.connect(getDefaultUrl())
                .timeout(30000)
                .userAgent(userAgent)
                .data(data)
                .method(Connection.Method.POST)
                .get();
		
		return getCampingItemMap(doc);
	}
}
