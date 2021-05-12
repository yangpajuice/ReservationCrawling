package com.tistory.yangpajuice.rc.service;

import java.util.*;

import javax.annotation.*;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;

import com.tistory.yangpajuice.rc.config.*;
import com.tistory.yangpajuice.rc.constants.*;
import com.tistory.yangpajuice.rc.item.*;
import com.tistory.yangpajuice.rc.param.*;
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
    private DbService dbService;
	
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
		telegram.sendSystemMessage(CodeConstants.SECT_ID_CAMP, getSiteName() + " is initialized");
	}
	
	private List<String> reservationDateList() {
		List<String> rtnList = new ArrayList<String>();
		
		try {
			ConfigParam param = new ConfigParam();
			param.setSectId(CodeConstants.SECT_ID_CAMP);
			param.setKeyId(CodeConstants.KEY_ID_DATE);
			List<ConfigItem> configItemList = dbService.getConfigItemList(param);
			if (configItemList != null && configItemList.size() > 0) {
				for (ConfigItem configItem : configItemList) {
					rtnList.add(configItem.getValue());
				}
			}
			
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
		
		return rtnList;
	}
	
	private void sendTelegramMessage(String text) {
		try {
			ConfigParam param = new ConfigParam();
			param.setSectId(CodeConstants.SECT_ID_CAMP);
			param.setKeyId(CodeConstants.KEY_ID_TELEGRAM);
			List<ConfigItem> configItemList = dbService.getConfigItemList(param);
			if (configItemList != null && configItemList.size() > 0) {
				for (ConfigItem configItem : configItemList) {
					if (configItem.getValue().equals(getSiteName()) == true) {
						if (configItem.getValue2().equals(CodeConstants.YES) == true) {
							telegram.sendMessage(CodeConstants.SECT_ID_CAMP, text);
							break;
						}
					}
				}
			}
			
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
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
					sendTelegramMessage("*** " + getSiteName() + " Open Date : " + dateDesc);
					
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
								sendTelegramMessage("*** " + getSiteName() + " available : " + dateDesc + " " + campingItem.toString());
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
		List<String> reservationDateList = reservationDateList();
		if (reservationDateList == null || reservationDateList.size() == 0) { // 설정이 없는 경우 31일 조회
			int days = 0;
			while (days < MAX_DAYS) {
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DAY_OF_YEAR, days);
				dateList.add(cal);
				
				days++;
			}
		} else { // 설정이 있는 경우 설정일만 조회
			for (String dateString : reservationDateList) {
				logger.info("reservationDate = " + dateString);
				
				Calendar cal = StrUtil.toCalendarFormat(DATE_FORMAT, dateString);
				Calendar calNow = StrUtil.toCalendarFormat(DATE_FORMAT, StrUtil.getCurDate()); //Calendar.getInstance();
				if (cal.before(calNow) == true) {
					logger.info("reservationDate is over = " + dateString);
					continue;
				}
				
				dateList.add(cal);
			}
		}

		for (Calendar cal : dateList) {
			Map<String, CampingItem> campingItemMap = getCampingItemMap(cal);
			
			String reservationDate = StrUtil.toDateFormat(DATE_FORMAT, cal);
			CampingParam param = new CampingParam();
			param.setSite(getSiteName());
			param.setReservatinDate(reservationDate);
			int seq = dbService.getMaxSeq(param);
			seq += 1;
			logger.debug("reservationDate = " + reservationDate + " / seq = " + seq);
			
			boolean nonState = false;
			for (String key : campingItemMap.keySet()) {
				CampingItem campingItem = campingItemMap.get(key);
				campingItem.setSeq(seq);
				campingItem.setSite(getSiteName());
				campingItem.setReservatinDate(reservationDate);
				campingItem.setInsertedDate(StrUtil.getCurDateTime());
				dbService.insertCampingItem(campingItem);
				
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
