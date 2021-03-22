package com.tistory.yangpajuice.rc.service;

import java.util.*;

import javax.annotation.*;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import com.gargoylesoftware.htmlunit.html.*;
import com.tistory.yangpajuice.rc.config.*;
import com.tistory.yangpajuice.rc.constants.*;
import com.tistory.yangpajuice.rc.item.*;
import com.tistory.yangpajuice.rc.param.*;
import com.tistory.yangpajuice.rc.util.*;

@Service
public class ClienService implements IService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final String baseUrl = "https://www.clien.net";
	
	@Autowired
	private Telegram telegram;
	
	@Autowired
	private TelegramClienAlarmConfig telegramClienAlarmConfig;
	
	@Autowired
    private DbService dbService;
	
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
			ConfigParam param = new ConfigParam();
			param.setSectId(CodeConstants.SECT_ID_CLIEN);
			param.setKeyId(CodeConstants.KEY_ID_URL);
			List<ConfigItem> configItemList = dbService.getConfigItemList(param);
			if (configItemList != null && configItemList.size() > 0) {
				for (ConfigItem configItem : configItemList) {
					String url = configItem.getValue();
					logger.info("URL = " + url);
					
					Document doc = Jsoup.connect(url).userAgent(
							"Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36")
							.timeout(30000)
							.referrer("http://www.google.com").get();
					
					
					Map<String, ClienEventItem> items = getClienItems(url, doc);
					rtnValue.putAll(items);
				}
			}
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
		
		return rtnValue;
	}
	
	private Map<String, ClienEventItem> getClienItems(String url, Document doc) {
		Map<String, ClienEventItem> rtnValue = new TreeMap<String, ClienEventItem>(Collections.reverseOrder());
		
		try {
			if (doc == null) {
				return rtnValue;
			}
			
			String mainCategory = doc.selectFirst("div h2").text();
			logger.info("Main Category = " + mainCategory);
			
			Elements elms = doc.select("span.list_subject");
			for (Element elm : elms) {
				String subject = elm.attr("title");
				
				Element subElm = elm.selectFirst("a");
				String subUrl = subElm.attr("href");
				
				ClienEventItem clienEventItem = new ClienEventItem();
				clienEventItem.setId(subUrl);
				clienEventItem.setDesc(subject);
				clienEventItem.setUrl(baseUrl + subUrl);
				
				rtnValue.put(clienEventItem.getId(), clienEventItem);
			}
			
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
		
		return rtnValue;
	}
}
