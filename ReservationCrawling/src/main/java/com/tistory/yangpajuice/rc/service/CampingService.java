package com.tistory.yangpajuice.rc.service;

import java.util.*;

import javax.annotation.*;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;

import com.tistory.yangpajuice.rc.constants.*;
import com.tistory.yangpajuice.rc.item.*;
import com.tistory.yangpajuice.rc.param.*;
import com.tistory.yangpajuice.rc.util.*;

public abstract class CampingService implements IService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36";
	private final String DATE_FORMAT = "yyyyMMdd";
	private final int MAX_DAYS = 31;
//	private Map<String, Map<String, CampingItem>> cacheCampingItemDateMap = null;
	
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
//			cacheCampingItemDateMap = getCampingItemDateMap();
			
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
		
		logger.info("Initialized");
		telegram.sendSystemMessage(CodeConstants.SECT_ID_CAMP, getSiteName() + " is initialized");
	}
	
	private List<Calendar> reservationDateList() {
		List<Calendar> rtnList = new ArrayList<Calendar>();
		
		try {
			List<String> dateList = new ArrayList<String>();
			ConfigParam param = new ConfigParam();
			param.setSectId(CodeConstants.SECT_ID_CAMP);
			param.setKeyId(CodeConstants.KEY_ID_DATE);
			List<ConfigItem> configItemList = dbService.getConfigItemList(param);
			if (configItemList != null && configItemList.size() > 0) {
				for (ConfigItem configItem : configItemList) {
					dateList.add(configItem.getValue());
				}
			}
			
			if (dateList == null || dateList.size() == 0) { // 설정이 없는 경우 31일 조회
				int days = 0;
				while (days < MAX_DAYS) {
					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.DAY_OF_YEAR, days);
					rtnList.add(cal);
					
					days++;
				}
			} else { // 설정이 있는 경우 설정일만 조회
				for (String dateString : dateList) {
					logger.info("reservationDate = " + dateString);
					
					Calendar cal = StrUtil.toCalendarFormat(DATE_FORMAT, dateString);
					Calendar calNow = StrUtil.toCalendarFormat(DATE_FORMAT, StrUtil.getCurDate()); //Calendar.getInstance();
					if (cal.before(calNow) == true) {
						logger.info("reservationDate is over = " + dateString);
						continue;
					}
					
					rtnList.add(cal);
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
	
//	private void removeOldCacheData() throws Exception {
//		// remove old data
//		Calendar yesterdayCal = Calendar.getInstance();
//		yesterdayCal.add(Calendar.DAY_OF_YEAR, -1);
//		String parsedDate = StrUtil.toDateFormat(DATE_FORMAT, yesterdayCal);
//		if (cacheCampingItemDateMap.remove(parsedDate) != null) {
//			logger.info("removed date = " + parsedDate);
//		}
//	}
	
	@Override
	public void start() {
		logger.info("START");
		
		try {
//			removeOldCacheData();
			
			Map<String, Map<String, CampingItem>> campingItemDateMapFromWeb = getCampingItemDateMapFromWeb();
			Map<String, Map<String, CampingItem>> campingItemDateMapFromDb = getCampingItemDateMapFromDb();
			
			for (String key : campingItemDateMapFromWeb.keySet()) {
				String dateDesc = StrUtil.toDateFormat(DATE_FORMAT, "yyyy-MM-dd EEE", key);
				Map<String, CampingItem> campingItemMap = campingItemDateMapFromWeb.get(key);
				Map<String, CampingItem> cacheCampingItemMap = campingItemDateMapFromDb.get(key);
				
				if (cacheCampingItemMap == null || cacheCampingItemMap.size() == 0) { // new
					for (String mapKey : campingItemMap.keySet()) {
						CampingItem campingItem = campingItemMap.get(mapKey);
						campingItem.setInsertedDate(StrUtil.getCurDateTime());
						dbService.insertCampingItem(campingItem);
					}

					logger.info("new date = " + key);
					sendTelegramMessage("▷ " + getSiteName() + " Open Date : " + dateDesc);
					
				} else {
					for (String itemKey : campingItemMap.keySet()) {
						CampingItem campingItem = campingItemMap.get(itemKey);
						CampingItem cacheCampingItem = cacheCampingItemMap.get(itemKey);
						
						if (cacheCampingItem.getState().equals(campingItem.getState()) == false) { // 기존 데이터 백업. 새 데이터 추가
							CampingParam param = new CampingParam();
							param.setSite(campingItem.getSite());
							param.setReservatinDate(campingItem.getReservatinDate());
							param.setArea(campingItem.getArea());
							param.setNo(campingItem.getNo());
							dbService.increaseSeqCampingItem(param);
							
							campingItem.setInsertedDate(StrUtil.getCurDateTime());
							dbService.insertCampingItem(campingItem);
						}

						// check data
						if (cacheCampingItem.getState().equals(CampingState.COMPLETED) == true || 
								cacheCampingItem.getState().equals(CampingState.WAITING) == true) {
							// completed/waiting ==> available
							if (campingItem.getState().equals(CampingState.AVAILABLE) == true) {
								logger.info("[" + key + "] available = " + campingItem.toString());
								
								String msg = "▶ " + getSiteName() + " " + campingItem.getState() + "\n";
								msg += "Date : " + dateDesc + "\n";
								msg += "Area : " + campingItem.getArea() + " " + campingItem.getNo() + "\n";
								msg += "\n";
								msg += getDefaultUrl();
								sendTelegramMessage(msg);
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
	
	private Map<String, Map<String, CampingItem>> getCampingItemDateMapFromWeb() throws Exception {
		Map<String, Map<String, CampingItem>> campingItemDateMap = new HashMap<String, Map<String,CampingItem>>();
		
		List<Calendar> dateList = reservationDateList();
		
		for (Calendar cal : dateList) {
			Map<String, CampingItem> campingItemMap = getCampingItemMap(cal);
			String reservationDate = StrUtil.toDateFormat(DATE_FORMAT, cal);
			
			boolean nonState = false;
			for (String key : campingItemMap.keySet()) {
				CampingItem campingItem = campingItemMap.get(key);
				campingItem.setSeq(0);
				campingItem.setSite(getSiteName());
				campingItem.setReservatinDate(reservationDate);
				campingItem.setInsertedDate(StrUtil.getCurDateTime());
				
				if (campingItem.getState().equals(CampingState.NONE) == true) {
					nonState = true;
				}
			}
			if (nonState == true) {
				logger.debug("not open = " + StrUtil.toDateFormat(DATE_FORMAT, cal));
				break;
			}

			campingItemDateMap.putIfAbsent(reservationDate, campingItemMap);
			logger.info(reservationDate + " count = " + campingItemMap.size());

			Thread.sleep(1000);
		}
		
		return campingItemDateMap;
	}
	
	private Map<String, Map<String, CampingItem>> getCampingItemDateMapFromDb() throws Exception {
		Map<String, Map<String, CampingItem>> campingItemDateMap = new HashMap<String, Map<String,CampingItem>>();
		
		List<Calendar> dateList = reservationDateList();

		for (Calendar cal : dateList) {
			String reservationDate = StrUtil.toDateFormat(DATE_FORMAT, cal);
			CampingParam param = new CampingParam();
			param.setSeq(0);
			param.setSite(getSiteName());
			param.setReservatinDate(reservationDate);
			List<CampingItem> campingItemList = dbService.getCampingItemList(param);

			Map<String, CampingItem> campingItemMap = new HashMap<String, CampingItem>();
			for (CampingItem item : campingItemList) {
				for (CampingState state : CampingState.values()) {
					if (state.toString().equals(item.getStateDesc()) == true) {
						item.setState(state);
						break;
					}
				}
				
				campingItemMap.put(item.getKey(), item);
			}

			campingItemDateMap.putIfAbsent(reservationDate, campingItemMap);
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
