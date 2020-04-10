package com.tistory.yangpajuice.rc.service;

import java.util.*;

import javax.annotation.*;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import com.tistory.yangpajuice.rc.item.*;
import com.tistory.yangpajuice.rc.util.*;

@Service
public class DaejeocampingService implements IService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36";
	private final String url = "https://www.daejeocamping.com/Camp.mobiz?camptype=camp01";
	private final String SITE_NAME = "DaejeoCamping";
	private Map<String, Map<String, CampingItem>> cacheCampingItemDateMap = null;
	
	@Autowired
	Telegram telegram;
	
	@PostConstruct
    private void init() {
		
		try {
			cacheCampingItemDateMap = getCampingItemDateMap();
			
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
		logger.info("Initialized");
	}
	
	@Override
	public void start() {
		logger.info("START");
		
		try {
			// remove old data
			Calendar yesterdayCal = Calendar.getInstance();
			yesterdayCal.add(Calendar.DAY_OF_YEAR, -1);
			String parsedDate = StrUtil.toDateFormat("yyyyMMdd", yesterdayCal);
			if (cacheCampingItemDateMap.remove(parsedDate) != null) {
				logger.info("removed date = " + parsedDate);
			}
			
			Map<String, Map<String, CampingItem>> campingItemDateMap = getCampingItemDateMap();
			for (String key : campingItemDateMap.keySet()) {
				String dateDesc = StrUtil.toDateFormat("yyyyMMdd", "yyyy-MM-dd EEE", key);
				Map<String, CampingItem> campingItemMap = campingItemDateMap.get(key);
				Map<String, CampingItem> cacheCampingItemMap = cacheCampingItemDateMap.get(key);
				
				if (cacheCampingItemMap == null) {
					cacheCampingItemDateMap.put(key, campingItemMap);
					logger.info("new date = " + key);
					telegram.sendMessage("*** " + SITE_NAME + " Open Date : " + dateDesc);
					
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
								telegram.sendMessage("*** " + SITE_NAME + " available : " + dateDesc + " " + campingItem.toString());
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
		
		Calendar cal = Calendar.getInstance();
		while (true) {
			cal.add(Calendar.DAY_OF_YEAR, 1);
			
			if (checkDate(cal) == false) {
				continue;
			}
			
			Map<String, CampingItem> campingItemMap = getCampingItemMap(cal);
			
			boolean nonState = false;
			for (String key : campingItemMap.keySet()) {
				CampingItem campingItem = campingItemMap.get(key);
				if (campingItem.getState().equals(CampingState.NONE) == true) {
					nonState = true;
				}
			}
			if (nonState == true) {
				logger.debug("not open = " + StrUtil.toDateFormat("yyyyMMdd", cal));
				break;
			}

			String parsedDate = StrUtil.toDateFormat("yyyyMMdd", cal);
			campingItemDateMap.putIfAbsent(parsedDate, campingItemMap);

			Thread.sleep(1000);
		}
		
		return campingItemDateMap;
	}
	
	private boolean checkDate(Calendar cal) {
		// weekend
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
			return true;
		}
		
		return false;
	}
	
	private Map<String, CampingItem> getCampingItemMap(Calendar cal) throws Exception {
		Map<String, CampingItem> campingItemMap = new HashMap<>();
		
		String parsedDate = StrUtil.toDateFormat("yyyy-MM-dd", cal);
		Map<String, String> data = new HashMap<>();
		data.put("resdate", parsedDate);
		
		Document doc = Jsoup.connect(url)
                .timeout(30000)
                .userAgent(userAgent)
                .data(data)
                .method(Connection.Method.POST)
                .get();
		
		Elements elms = doc.select("fieldset.ui-area");
		for (Element elm : elms) {
			CampingItem campingItem = getCampingItem(elm);
			if (campingItem == null) {
				continue;
			}
			
			campingItemMap.put(campingItem.getKey(), campingItem);
			logger.debug("[" + parsedDate + "] inquiry = " + campingItem.toString());
		}
		
		return campingItemMap;
	}
	
	private CampingItem getCampingItem(Element elm) throws Exception {
		CampingItem campingItem = new CampingItem();
		campingItem.setState(CampingState.AVAILABLE);
		
		String[] clazzList = elm.attr("class").split("ui-");
		for (String clazz : clazzList) {
			clazz = clazz.trim();
			if (clazz.startsWith("type") == true) { // ex) type-A
				String area = clazz.substring(clazz.length() - 1, clazz.length());
				campingItem.setArea(area);
				
			} else if (clazz.startsWith("state-comp") == true) {
				campingItem.setState(CampingState.COMPLETED);
				
			} else if (clazz.startsWith("state-wait") == true) {
				campingItem.setState(CampingState.WAITING);
				
			} else if (clazz.startsWith("state-none") == true) {
				campingItem.setState(CampingState.NONE);
			}
		}
		
		Elements labelElms = elm.select("label");
		String no = labelElms.text();
		campingItem.setNo(no);
		
		return campingItem;
	}
}
