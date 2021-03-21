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
	private final String URL = "https://www.nakdongcamping.com/reservation/real_time";
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
		Elements elms = doc.select("div.click_inner a");
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
		
		String no = elm.childNode(0).toString();
		campingItem.setNo(no);
		
		String area = elm.childNode(3).attr("value");
		campingItem.setArea(area);
		
		String[] clazzList = elm.attr("class").split("  ");
		for (String clazz : clazzList) {
			clazz = clazz.trim();
			if (clazz.startsWith("cbtn_Pcomplete") == true) {
				campingItem.setState(CampingState.COMPLETED);
				
			} else if (clazz.startsWith("cbtn_Pwaiting") == true) {
				campingItem.setState(CampingState.WAITING);
				
			} else if (clazz.startsWith("cbtn_Pnone") == true) {
				campingItem.setState(CampingState.NONE);
			}
		}
		
		return campingItem;
	}
}
