package com.tistory.yangpajuice.rc.service;

import java.util.*;

import org.jsoup.nodes.*;
import org.jsoup.select.*;
import org.slf4j.*;
import org.springframework.stereotype.*;

import com.tistory.yangpajuice.rc.item.*;
import com.tistory.yangpajuice.rc.util.*;

@Service
public class HwaMyungCampingService  extends CampingService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final String URL = "https://www.hwamyungcamping.com/reservation/real_time";
	private final String SITE_NAME = "HwaMyungCamping";
	
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
		Elements elms = doc.select("a.cbtn");
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
		String area = elm.text();
		campingItem.setArea(area);

		String[] clazzList = elm.attr("class").split("cbtn_");
		for (String clazz : clazzList) {
			clazz = clazz.trim();
			if (clazz.startsWith("on") == true) {
				campingItem.setState(CampingState.AVAILABLE);
				
			} else if (clazz.startsWith("Pcomplete") == true) {
				campingItem.setState(CampingState.COMPLETED);
				
			} else if (clazz.startsWith("Pwaiting  ") == true) {
				campingItem.setState(CampingState.WAITING);
			}
		}
		
		Elements labelElms = elm.select("label");
		String no = labelElms.text();
		campingItem.setNo(no);
		
		return campingItem;
	}
}
