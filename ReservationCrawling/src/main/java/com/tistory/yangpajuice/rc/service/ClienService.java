package com.tistory.yangpajuice.rc.service;

import java.util.*;

import javax.annotation.*;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import com.gargoylesoftware.htmlunit.html.*;
import com.tistory.yangpajuice.rc.config.*;
import com.tistory.yangpajuice.rc.item.*;
import com.tistory.yangpajuice.rc.util.*;

@Service
public class ClienService implements IService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final String baseUrl = "https://www.clien.net";
	private final String jirumUrl = "https://www.clien.net/service/board/jirum";
	
	@Autowired
	private Telegram telegram;
	
	@Autowired
	private TelegramClienAlarmConfig telegramClienAlarmConfig;
	
	private Map<String, ClienEventItem> prevItems;
	
	@PostConstruct
    private void init() {
		prevItems = getClienItems();
		Iterator<String> keys = prevItems.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            ClienEventItem value = prevItems.get(key);
            logger.info("ClienService.init() PrevItem = " + value.getDesc());
        }
        
        telegram.sendMessage(telegramClienAlarmConfig, "Clien is initialized");
	}
	
	@Override
	public void start() {
		logger.info("#### ClienService is started #### ");
		
		try {
			Map<String, ClienEventItem> clienItems = getClienItems();
			
			Iterator<String> keys = clienItems.keySet().iterator();
	        while (keys.hasNext()) {
	            String key = keys.next();
	            ClienEventItem value = clienItems.get(key);
	        	if (prevItems.containsKey(key) == true) {
	        		continue;
	        	}
	        	prevItems.put(key, value);
	        	
	        	String msg = "[Clien]\n";
	        	msg += value.getDesc() + "\n";
	        	msg += value.getUrl();
	        	telegram.sendMessage(telegramClienAlarmConfig, msg);
	        	Thread.sleep(100);
	        }
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
	}
	
	private Map<String, ClienEventItem> getClienItems() {
		Map<String, ClienEventItem> rtnValue = new TreeMap<String, ClienEventItem>(Collections.reverseOrder());
		
		try {
			HtmlPage page = Utils.getPage(jirumUrl);
			List<HtmlSpan> eventList = page.getByXPath("//span[@class='list_subject']");
			
			for (int i = 0; i < eventList.size(); i++) {
				String subUrl = "";
				String desc = "";
				
				HtmlSpan event = eventList.get(i);
				desc = event.getAttribute("title");
				
				HtmlAnchor targetNode = (HtmlAnchor) event.getChildNodes().get(1);
				subUrl = targetNode.getAttribute("href");
				
				
				ClienEventItem clienEventItem = new ClienEventItem();
				clienEventItem.setId(subUrl);
				clienEventItem.setDesc(desc);
				clienEventItem.setUrl(baseUrl + subUrl);
				
				rtnValue.put(clienEventItem.getId(), clienEventItem);
			}
			
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
		
		return rtnValue;
	}
}
