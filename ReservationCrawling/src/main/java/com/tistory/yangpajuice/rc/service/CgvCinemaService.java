package com.tistory.yangpajuice.rc.service;

import java.util.*;

import javax.annotation.*;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.w3c.dom.*;

import com.gargoylesoftware.htmlunit.html.*;
import com.tistory.yangpajuice.rc.config.*;
import com.tistory.yangpajuice.rc.item.*;
import com.tistory.yangpajuice.rc.util.*;

@Service
public class CgvCinemaService implements IService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final String eventBaseUrl = "http://m.cgv.co.kr";
	private final String eventBaseUrl2 = "http://m.cgv.co.kr/WebApp/EventNotiV4";
	private final String previewStageGreetingUrl = "http://m.cgv.co.kr/WebApp/EventNotiV4/eventMain.aspx/EventDetailGeneral.aspx?mCode=002&amp;iPage=1";
	
	@Autowired
	private Telegram telegram;
	
	@Autowired
	private CinemaAlarmConfig cinemaAlarmConfig;
	
	private Map<String, CgvCinemaEventItem> prevItems;
	
	@PostConstruct
    private void init() {
		prevItems = getCgvItems();
		Iterator<String> keys = prevItems.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            CgvCinemaEventItem value = prevItems.get(key);
            logger.info("CgvCinemaService.init() PrevItem = " + key + " / " + value.getDescription());
        }
        
        telegram.sendMessage(cinemaAlarmConfig, "CgvCinema is initialized");
	}
	
	@Override
	public void start() {
		logger.info("#### CgvCinemaService is started #### ");
		
		try {
			Map<String, CgvCinemaEventItem> gcvItems = getCgvItems();
			
			Iterator<String> keys = gcvItems.keySet().iterator();
	        while (keys.hasNext()) {
	            String key = keys.next();
	            CgvCinemaEventItem value = gcvItems.get(key);
	        	if (prevItems.containsKey(key) == true) {
	        		continue;
	        	}
	        	prevItems.put(key, value);
	        	
	        	String msg = "[CGV]\n";
	        	msg += value.getDescription() + "\n";
	        	msg += value.getDate() + "\n";
	        	msg += value.getLink() + "\n";
	        	telegram.sendMessage(cinemaAlarmConfig, msg);
	        	Thread.sleep(100);
	        }
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
	}
	
	private Map<String, CgvCinemaEventItem> getCgvItems() {
		Map<String, CgvCinemaEventItem> rtnValue = new TreeMap<String, CgvCinemaEventItem>(Collections.reverseOrder());
		
		try {
			HtmlPage page = Utils.getPage(previewStageGreetingUrl);

			List<HtmlListItem> eventList = page.getByXPath("//li[@class='sponsorFpType1']");
			List<HtmlSpan> titleList = page.getByXPath("//span[@class='sponsorFpTxt']");
			List<HtmlSpan> dateList = page.getByXPath("//span[@class='sponsorFpPeriod']");
			
			for (int i = 0; i < eventList.size(); i++) {
				String subUrl = "";
				String desc = "";
				String date = "";
				String id = "";
				
				id = eventList.get(i).getId();
				
				DomNode targetNode = eventList.get(i).getChildNodes().get(1);
				Node urlAttr = targetNode.getAttributes().item(0);
				subUrl = urlAttr.getTextContent();
				subUrl = subUrl.split("'")[1];
				
				DomNode titletNode = (DomNode) titleList.get(i);
				desc = titletNode.asText();
				
				DomNode dateNode = (DomNode) dateList.get(i);
				date = dateNode.asText();
				
				CgvCinemaEventItem cgvCinemaEventItem = new CgvCinemaEventItem();
				cgvCinemaEventItem.setId(id);
				cgvCinemaEventItem.setDescription(desc);
				cgvCinemaEventItem.setDate(date);
				
				if (subUrl.startsWith("/WebApp") == true) {
					cgvCinemaEventItem.setLink(eventBaseUrl + subUrl);
				} else {
					cgvCinemaEventItem.setLink(eventBaseUrl2 + subUrl.substring(1));
				}
				
				rtnValue.put(id, cgvCinemaEventItem);
			}
			
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
		
		return rtnValue;
	}
}
