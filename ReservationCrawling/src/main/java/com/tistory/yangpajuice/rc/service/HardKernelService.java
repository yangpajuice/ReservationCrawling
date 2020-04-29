package com.tistory.yangpajuice.rc.service;

import java.util.*;

import javax.annotation.*;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import com.tistory.yangpajuice.rc.config.*;
import com.tistory.yangpajuice.rc.item.*;
import com.tistory.yangpajuice.rc.util.*;

@Service
public class HardKernelService implements IService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final String hardKernel = "https://www.hardkernel.com/ko/product-category/odroid-board/";
	
	@Autowired
	private Telegram telegram;
	
	@Autowired
	private TelegramHardkernelAlarmConfig telegramHardkernelAlarmConfig;
	
	private Map<String, HardKernelItem> prevItems;
	
	@PostConstruct
    private void init() {
		prevItems = getHardKernelItems();
		Iterator<String> keys = prevItems.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();            
            logger.info("AsusFirmwareService.init() PrevItem = " + key);
        }
        telegram.sendMessage(telegramHardkernelAlarmConfig, "HardKernel is initialized");
	}
	
	@Override
	public void start() {
		logger.info("#### HardKernelService is started ####");
		
		try {
			Map<String, HardKernelItem> itemMap = getHardKernelItems();
			Iterator<String> keys = itemMap.keySet().iterator();
	        while (keys.hasNext()) {
	            String key = keys.next();
	            HardKernelItem value = itemMap.get(key);
	            
	            if (prevItems.containsKey(key) == true) {
	            	HardKernelItem prevItem = prevItems.get(key);
	            	if (value.getPrice().equals(prevItem.getPrice()) == false) { // 가격 변경
		            	String msg = "[HardKernel] 가격 변경 : " + value.getName() + "\n";
		            	msg += prevItem.getPrice() + " => " + "\n";
		            	msg += value.getPrice() + " => ";
			        	telegram.sendMessage(msg);
	            	}

	        	} else {
	        		prevItems.put(key, value);
	        		
	            	String msg = "[HardKernel] New : " + value.getName() + "\n";
	            	msg += value.getPrice();
		        	telegram.sendMessage(telegramHardkernelAlarmConfig, msg);
	        	}

	        	Thread.sleep(100);
	        }
		
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
	}
	
	private Map<String, HardKernelItem> getHardKernelItems() {
		Map<String, HardKernelItem> rtnValue = new TreeMap<String, HardKernelItem>(Collections.reverseOrder());
		
		try {
			Document doc = Jsoup.connect(hardKernel).userAgent(
					"Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36")
					.referrer("http://www.google.com").get();

			setItem(rtnValue, doc);
			
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
		
		return rtnValue;
	}

	private void setItem(Map<String, HardKernelItem> rtnValue, Document doc) {
		Elements elms = doc.select("div.box-text");
		for (Element elm : elms) {
			Elements elmsItems = elm.select("p.name a");
			if (elmsItems.size() == 0) {
				continue;
			}
			String name = elmsItems.text();
			
			elmsItems = elm.select("span.woocommerce-Price-amount");
			if (elmsItems.size() == 0) {
				continue;
			}
			String price = elmsItems.text();
			
			HardKernelItem hardKernelItem = new HardKernelItem();
			hardKernelItem.setName(name);
			hardKernelItem.setPrice(price);
			
			rtnValue.put(name, hardKernelItem);
		}
	}
}
