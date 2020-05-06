package com.tistory.yangpajuice.rc.service;

import java.util.*;

import javax.annotation.*;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.stereotype.*;
import org.springframework.util.*;

import com.tistory.yangpajuice.rc.config.*;
import com.tistory.yangpajuice.rc.item.*;
import com.tistory.yangpajuice.rc.util.*;

@Service
public class LotteCinemaService implements IService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	// 시사회 _무대인사
	private final String previewStageGreetingUrl = "http://www.lottecinema.co.kr/LCWS/Event/EventData.aspx";
	private final String previewStageGreetingDetailUrl = "https://www.lottecinema.co.kr/NLCMW/Event/EventTemplateStageGreeting?eventId=";
	
	private final String cinemaMallUrl = "http://www.lottecinema.co.kr/LCWS/CinemaMall/CinemaMallData.aspx";

	@Autowired
	private Telegram telegram;
	
	@Autowired
	private CinemaAlarmConfig cinemaAlarmConfig;
	
	private Map<String, LotteCinemaEventItem> prevItems;
	private Map<String, ProductItem> mallItems;
	
	@PostConstruct
    private void init() {
		try {
			prevItems = new HashMap<String, LotteCinemaEventItem>();
			LotteCinemaEventResponse response = getEventResponse();
			for (LotteCinemaEventItem item : response.getItems()) {
	        	String key = item.getEventID();
	        	prevItems.put(key, item);
	        	
	        	logger.info("LotteCinemaService.init() PrevItem = " + key + "/" + item.getEventName());
			}
			
			mallItems = new HashMap<String, ProductItem>();
			LotteCinemaResponse cinemaResponse = getCinemaData();
	        List<ProductItem> productItems = cinemaResponse.getLCMall_Main_Items().getProduct_Items().getItems();
	        for (ProductItem item : productItems) {
	        	String key = item.getDisplayItemID();
	        	mallItems.put(key, item);
	        }
	        
	        telegram.sendMessage(cinemaAlarmConfig, "LotteCinema is initialized");
	        
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
	}
	
	@Override
	public void start() {
		logger.info("#### LotteCinemaService is started ####");
		
		try {
	        setEventResponse();
	        setCinemaResponse();
		
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
	}

	private void setCinemaResponse() throws InterruptedException {
		LotteCinemaResponse cinemaResponse = getCinemaData();
		if (cinemaResponse == null || cinemaResponse.getLCMall_Main_Items() == null) {
			logger.info("LotteCinemaService.setCinemaResponse() cinemaResponse does not exist.");
			return;
		}
		
		List<ProductItem> productItems = cinemaResponse.getLCMall_Main_Items().getProduct_Items().getItems();
		if (productItems.size() == 0) {
			logger.info("LotteCinemaService.setCinemaResponse() cinemaResponse Item does not exist.");
			return;
		}
		for (ProductItem item : productItems) {
			String key = item.getDisplayItemID();
			if (mallItems.containsKey(key) == true) {
				continue;
			}
			
			String msg = "[롯데시네마]\n";
			msg += item.getDisplayItemName() + "\n";
			
			String buyLink = String.format(
		             "https://www.lottecinema.co.kr/NLCMW/CinemaMall/Detail?ItemId=%s&ClassificationCode=%s&MenuId=%s",
		             item.getDisplayItemID(), item.getDisplayLargeClassificationCode(), item.getMenuId()
		    );
			msg += buyLink;
			telegram.sendMessage(cinemaAlarmConfig, msg);
			Thread.sleep(100);
			
			mallItems.put(key, item);
		}
	}

	private void setEventResponse() throws InterruptedException {
		LotteCinemaEventResponse response = getEventResponse();
		if (response == null || response.getItems() == null) {
			logger.info("LotteCinemaService.setEventResponse() response does not exist.");
			return;
		}
		if (response.getItems().size() == 0) {
			logger.info("LotteCinemaService.setEventResponse() response Item does not exist.");
			return;
		}
		
		List<LotteCinemaEventItem> eventItems = response.getItems();
		for (LotteCinemaEventItem item : eventItems) {
			String key = item.getEventID();
			if (prevItems.containsKey(key) == true) {
				continue;
			}
			
			String msg = "[롯데시네마]\n";
			msg += item.getEventName() + "\n";
			msg += item.getProgressStartDate() + "~" + item.getProgressEndDate() + "\n";
			msg += previewStageGreetingDetailUrl + item.getEventID() + "\n";
			telegram.sendMessage(cinemaAlarmConfig, msg);
			Thread.sleep(100);
			
			prevItems.put(key, item);
		}
	}
	
	private LotteCinemaEventResponse getEventResponse() {
		LotteCinemaEventResponse response = null;
		
		try {
			HttpHeaders headers = new HttpHeaders();
	        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);

	        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
	        map.add("paramList",
	                "{\"MethodName\":\"GetEventLists\"," + 
	        		"\"channelType\":\"HO\"," + 
	                "\"osType\":\"Chrome\"," + 
	        		"\"osVersion\":\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36\"," +
					"\"EventClassificationCode\":\"40\"," +
					"\"MemberNo\":\"0\"," +
					"\"CinemaID\":\"\"," +
					"\"PageNo\":\"1\"," +
					"\"PageSize\":\"8\"," +        		
	        		"\"SearchText\":\"\"}");

	        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
	        String jsonResponse = Utils.restTemplate.postForObject(previewStageGreetingUrl, request, String.class);
	        
	        response = Utils.gson.fromJson(jsonResponse, LotteCinemaEventResponse.class);
	        
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
		
		return response;
	}
	
	private LotteCinemaResponse getCinemaData() {
		LotteCinemaResponse response = null;
		
		try {
	        HttpHeaders headers = new HttpHeaders();
	        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
	
	        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
	        map.add("paramList",
	                "{\"MethodName\":\"GetLCMallMain\",\"channelType\":\"MW\",\"osType\":\"Chrome\",\"osVersion\":\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36\",\"multiLanguageID\":\"KR\",\"menuID\":\"3\"}");
	
	        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
	
	        String jsonResponse = Utils.restTemplate.postForObject(cinemaMallUrl, request, String.class);
	        response = Utils.gson.fromJson(jsonResponse, LotteCinemaResponse.class);
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
		
		return response;
    }
	
	private ProductItem getDetailCinemaData(String itemID) {
		ProductItem productItem = null;
		
		try {
	        HttpHeaders headers = new HttpHeaders();
	        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
	
	        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
	        map.add("paramList",
	                 "{\"MethodName\":\"GetLCMallDetail\",\"channelType\":\"MW\",\"osType\":\"Chrome\",\"osVersion\":\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.86 Safari/537.36\",\"multiLanguageID\":\"KR\",\"menuID\":\"3\",\"itemID\":\"" + itemID + "\",\"classificationCode\":\"20\"}"
	        );
	
	        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
	
	        String jsonResponse = Utils.restTemplate.postForObject(cinemaMallUrl, request, String.class);
	        LotteCinemaResponse response = Utils.gson.fromJson(jsonResponse, LotteCinemaResponse.class);
	        
	        List<ProductItem> items = response.getLCMall_Detail_Items().getProduct_Items().getItems();
	        if (items != null && items.size() > 0) {
	        	productItem = items.get(0);
	        } else {
	        	System.out.println("A");
	        }
	        
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
		
		return productItem;
    }

}
