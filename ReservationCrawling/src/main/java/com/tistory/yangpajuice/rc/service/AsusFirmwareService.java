package com.tistory.yangpajuice.rc.service;

import java.util.*;

import javax.annotation.*;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.w3c.dom.*;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import com.tistory.yangpajuice.rc.config.*;
import com.tistory.yangpajuice.rc.item.*;
import com.tistory.yangpajuice.rc.util.*;

@Service
public class AsusFirmwareService implements IService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
private final String blueCaveurl = "https://www.asus.com/kr/Networking/Blue-Cave/HelpDesk_BIOS/";
	
	@Autowired
	private Telegram telegram;
	
	@Autowired
	private AsusFirmwareAlarmConfig asusFirmwareAlarmConfig;
	
	private Map<String, AsusFirmwareItem> prevItems;
	
	@PostConstruct
    private void init() {
		prevItems = getFirmwareItem();
		Iterator<String> keys = prevItems.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();            
            logger.info("AsusFirmwareService.init() PrevItem = " + key);
        }
        telegram.sendMessage(asusFirmwareAlarmConfig, "AsusFirmware is initialized");
	}
	
	@Override
	public void start() {
		logger.info("#### AsusFirmwareService is started ####");
		
		try {
			Map<String, AsusFirmwareItem> itemMap = getFirmwareItem();
			Iterator<String> keys = itemMap.keySet().iterator();
	        while (keys.hasNext()) {
	            String key = keys.next();
	            AsusFirmwareItem value = itemMap.get(key);
	            
	            if (prevItems.containsKey(key) == true) {
	        		continue;
	        	}
	            prevItems.put(key, value);

	            String msg = "[Asus Firmware] " + value.getDate() + "\n";
	        	msg += value.getVersion() + "\n";
	        	msg += value.getFileByte() + "\n" + "\n";
	        	msg += value.getDesc().substring(0, 100) + "..." + "\n";
	        	telegram.sendMessage(asusFirmwareAlarmConfig, msg);
	        	Thread.sleep(100);
	        }
		
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
	}
	
	private HtmlDivision getHtmlElementById(HtmlPage page, String id) throws Exception {
		HtmlDivision rtnValue = null;
		boolean tryToGet = true;
		int tryToGetCnt = 0;
		
		while (tryToGet == true) {
			try {
				rtnValue = page.getHtmlElementById(id);
				
			} catch (ElementNotFoundException ex) {
				logger.error("An exception occurred!", ex);
				
				tryToGetCnt++;
				if (tryToGetCnt > 5) {
					throw ex;
				}
				Thread.sleep(3000);
				
			} finally {
				
			}
			
			if (rtnValue != null) {
				tryToGet = false;
			}
		}
		
		return rtnValue;
	}
	
	private Map<String, AsusFirmwareItem> getFirmwareItem() {
		Map<String, AsusFirmwareItem> rtnValue = new TreeMap<String, AsusFirmwareItem>(Collections.reverseOrder());
		
		try {
			HtmlPage page = Utils.getPage(blueCaveurl);
			HtmlDivision manualDownload = getHtmlElementById(page, "Manual-Download");
			
			List<HtmlDivision> versionDivs = manualDownload.getByXPath("//div[@class='download-inf-l']");
			for (HtmlDivision div : versionDivs) {
				String version = "";
				String date = "";
				String fileByte = "";
				String des = "";
				
				for (DomNode domNode : div.getChildren()) {
					Node attr = domNode.getAttributes().item(0);
					if (attr.getTextContent().equals("version") == true) {
						version = domNode.asText();
					} else if (attr.getTextContent().equals("lastdate") == true) {
						date = domNode.asText();
					} else if (attr.getTextContent().equals("byte") == true) {
						fileByte = domNode.asText();
					} else if (attr.getTextContent().equals("des") == true) {
						des = domNode.asText();
					}
				}
				
				if (version != null && version.length() > 0) {
					AsusFirmwareItem asusFirmwareItem = new AsusFirmwareItem();
					asusFirmwareItem.setVersion(version);
					asusFirmwareItem.setDate(date);
					asusFirmwareItem.setFileByte(fileByte);
					asusFirmwareItem.setDesc(des);
					
					rtnValue.put(version, asusFirmwareItem);
				}
			}
			
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
		
		return rtnValue;
	}
}
