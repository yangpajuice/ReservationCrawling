package com.tistory.yangpajuice.rc.service.cinema;

import java.util.*;

import javax.annotation.*;

import org.jsoup.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.util.*;
import org.springframework.web.reactive.function.*;
import org.springframework.web.reactive.function.client.*;

import com.google.gson.*;
import com.tistory.yangpajuice.rc.constants.*;
import com.tistory.yangpajuice.rc.item.*;
import com.tistory.yangpajuice.rc.param.*;
import com.tistory.yangpajuice.rc.service.*;
import com.tistory.yangpajuice.rc.util.*;

public class CgvCinemaService  implements IService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final String SITE = "CgvCinema";
	private final String NEW_LINE = "\n";
	
	@Autowired
	private Telegram telegram;

	@Autowired
	private DbService dbService;
	
	@PostConstruct
	private void init() {
		logger.info("init");
		
		try {
			
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
	}
	
	@PreDestroy
	public void destroy() {
		logger.info("destroy");
		
		try {
			
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
	}
	
	@Override
	public void start() {
		logger.info("START");
		
		try {
			WebClient webClient = WebClient.builder().baseUrl("https://www.lottecinema.co.kr").build();			
			MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
	        map.add("paramList", "{\"MethodName\":\"GetEventLists\",\"channelType\":\"MW\",\"osType\":\"I\",\"osVersion\":\"Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1\",\"EventClassificationCode\":\"0\",\"SearchText\":\"\",\"CinemaID\":\"\",\"PageNo\":1,\"PageSize\":12,\"MemberNo\":\"0\"}");

	        String eventList = webClient.post().uri("LCWS/Event/EventData.aspx")
					.body(BodyInserters.fromFormData(map)).exchange().block().bodyToMono(String.class).block();
			if (eventList == null) {
				logger.error("eventList is null");
				return;
			}
			
			Gson gson = new Gson();
			LotteCinemaItem lotteCinemaItem = gson.fromJson(eventList, LotteCinemaItem.class);
			if (lotteCinemaItem == null) {
				logger.error("lotteCinemaItem is null");
				return;
			}
			
			List<LotteCinemaEventItem> lotteCinemaEventItemList = lotteCinemaItem.getItems();
			if (lotteCinemaEventItemList == null || lotteCinemaEventItemList.size() == 0) {
				logger.error("lotteCinemaEventItemList is null");
				return;
			}
			
			WebPageParam webPageParam = new WebPageParam();
			webPageParam.setSite(SITE);
			List<WebPageItem> webPageItemListFromDb = dbService.getRecentWebPageItemList(webPageParam);
			for (LotteCinemaEventItem lotteCinemaEventItem : lotteCinemaEventItemList) {
				
				WebPageItem webPageItem = getWebPageItem(lotteCinemaEventItem);
				
				boolean existItem = false;
				for (WebPageItem webPageItemFromDb : webPageItemListFromDb) {
					if (webPageItemFromDb.getId().equals(webPageItem.getId()) == true) {
						existItem = true;
						break;
					}
				}
				
				if (existItem == true) {
					
				} else { // new item is added
					int insertedCnt = dbService.insertWebPageItem(webPageItem);
					
					// send message
					String message = "[롯데시네마]" + NEW_LINE;
					message += webPageItem.getSubject() + NEW_LINE;
					message += webPageItem.getArticle() + NEW_LINE;
					message += webPageItem.getUrl();
					telegram.sendMessage(CodeConstants.SECT_ID_CINEMA, message);
					Thread.sleep(100);
				}
			}
			
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
		
		logger.info("END");
	}
	
	private WebPageItem getWebPageItem(LotteCinemaEventItem item) {
		WebPageItem webPageItem = new WebPageItem();
		
		try {
			webPageItem.setSite(SITE);
			webPageItem.setInsertedDate(StrUtil.getCurDateTime());
			
			webPageItem.setId(item.getEventID());
			webPageItem.setUrl(item.getImageUrl());
			webPageItem.setMainCategory(item.getEventClassificationCode());
			webPageItem.setSubCategory(item.getEventTypeCode());
			webPageItem.setSubject(item.getEventName());
			
			String article = item.getEventNtc();
			if (article != null && article.length() > 0) {
				article = Jsoup.parse(article).text();
				webPageItem.setArticle(article);
			}
			
			String postDate = item.getProgressStartDate(); // 2021.05.27
			if (postDate != null && postDate.length() > 0) {
				postDate = StrUtil.toDateFormat("yyyy.MM.dd", "yyyyMMddHHmmss", postDate);
				webPageItem.setPostDate(postDate);
			}
			
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
		
		return webPageItem;
	}
}
