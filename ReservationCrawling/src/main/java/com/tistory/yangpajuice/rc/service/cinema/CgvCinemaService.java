package com.tistory.yangpajuice.rc.service.cinema;

import java.util.*;

import javax.annotation.*;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import org.slf4j.*;
import org.springframework.http.*;
import org.springframework.stereotype.*;
import org.springframework.util.*;
import org.springframework.web.reactive.function.*;
import org.springframework.web.reactive.function.client.*;

import com.google.gson.*;
import com.tistory.yangpajuice.rc.constants.*;
import com.tistory.yangpajuice.rc.item.*;
import com.tistory.yangpajuice.rc.item.event.*;
import com.tistory.yangpajuice.rc.param.*;
import com.tistory.yangpajuice.rc.util.*;

@Service
public class CgvCinemaService extends CinemaService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final String SITE = "CgvCinema";
	
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
			WebClient webClient = WebClient.builder().baseUrl("http://m.cgv.co.kr").defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();			
			String requestPayload = "{mC: '004',rC:'GEN' ,tC:'',iP:'1',pRow:'20',rnd6:'0',fList:''}";

	        String eventList = webClient.post().uri("/WebAPP/EventNotiV4/eventMain.aspx/getEventDataList")
					.body(BodyInserters.fromValue(requestPayload)).exchange().block().bodyToMono(String.class).block();
			if (eventList == null) {
				logger.error("eventList is null");
				return;
			}
			
			Document doc = Jsoup.parse(eventList);
			if (doc == null) {
				logger.error("eventList is null");
				return;
			}
			
			WebPageParam webPageParam = new WebPageParam();
			webPageParam.setSite(SITE);
			List<WebPageItem> webPageItemListFromDb = dbService.getRecentWebPageItemList(webPageParam);
			List<WebPageItem> webPageItemListFromWeb = new ArrayList<WebPageItem>(); 
			Elements elms = doc.getElementsByClass("sponsorFpType0");
			for (Element elm : elms) {
				WebPageItem webPageItem = new WebPageItem();
				
				webPageItem.setSite(SITE);
				webPageItem.setInsertedDate(StrUtil.getCurDateTime());
				
				String id = elm.attr("id"); // ex) event_seq_32351
				id = id.replace("event_seq_", ""); // prefix 제거				
				webPageItem.setId(id);
				
				Elements subElms = elm.select("a");
				String[] urlList = subElms.get(0).attr("href").split("'");
				String subUrl = urlList[1];
				if (subUrl.startsWith("./EventDetailGeneralUnited") == true) { // ex) ./EventDetailGeneralUnited.aspx?seq=32348&mCode=004&iPage=1
					subUrl = subUrl.substring(1);
					subUrl = "/WebApp/EventNotiV4/" + subUrl;
				}
				String url = "http://m.cgv.co.kr" + subUrl;
				webPageItem.setUrl(url);
				
				webPageItem.setMainCategory("EVENT");
				webPageItem.setSubCategory("영화");
				
				Element imgElm = elm.selectFirst("img");
				String subject = imgElm.attr("alt");
				webPageItem.setSubject(subject);
				
				webPageItem.setArticle("");
				webPageItem.setPostDate("");
				
				webPageItemListFromWeb.add(webPageItem);
			}
			
			for (WebPageItem webPageItem : webPageItemListFromWeb) {	
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
					
					publisher.publishEvent(new CgvAddedEvent(webPageItem));
					Thread.sleep(100);
				}
			}
			
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
		
		logger.info("END");
	}
}