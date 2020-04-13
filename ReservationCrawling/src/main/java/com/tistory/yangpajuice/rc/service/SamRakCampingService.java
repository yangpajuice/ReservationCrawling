package com.tistory.yangpajuice.rc.service;

import java.util.*;

import org.jsoup.nodes.*;
import org.jsoup.select.*;
import org.slf4j.*;
import org.springframework.stereotype.*;

import com.tistory.yangpajuice.rc.item.*;
import com.tistory.yangpajuice.rc.util.*;

@Service
public class SamRakCampingService extends CampingService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final String URL = "https://www.nakdongcamping.com/Camp.mobiz";
	private final String SITE_NAME = "SamRakCamping";
	
	@Override
	protected String getDefaultUrl() {
		return URL;
	}

	@Override
	protected String getSiteName() {
		return SITE_NAME;
	}

	@Override
	protected Map<String, String> getInquiryData(Calendar cal) {
		String parsedDate = StrUtil.toDateFormat("yyyy-MM-dd", cal);
		Map<String, String> data = new HashMap<>();
		data.put("resdate", parsedDate);
		
		return data;
	}

	@Override
	protected Map<String, CampingItem> getCampingItemMap(Document doc) throws Exception {
		Map<String, CampingItem> campingItemMap = new HashMap<>();
		Elements elms = doc.select("fieldset.ui-area");
		for (Element elm : elms) {
			CampingItem campingItem = getCampingItem(elm);
			if (campingItem == null) {
				continue;
			}
			
			campingItemMap.put(campingItem.getKey(), campingItem);
			logger.debug(campingItem.toString());
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
