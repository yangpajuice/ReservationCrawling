package com.tistory.yangpajuice.rc.service;

import java.util.*;

import javax.annotation.*;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.*;
import org.springframework.http.*;
import org.springframework.stereotype.*;
import org.springframework.web.reactive.function.client.*;

import com.google.gson.*;
import com.tistory.yangpajuice.rc.item.*;
import com.tistory.yangpajuice.rc.item.event.*;
import com.tistory.yangpajuice.rc.util.*;

@Service
public class InterparkService implements IService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	protected final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36";

	@Autowired
	protected DbService dbService;
	
	@Autowired
	protected ApplicationEventPublisher publisher;
	
	HashMap<String, InterparkItem> itemList = new HashMap<String, InterparkItem>();
	
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
			String link = "https://tickets.interpark.com/goods/21002185";
			String BASE_URL = "https://api-ticketfront.interpark.com";
			WebClient webClient = WebClient.builder().baseUrl(BASE_URL)
					.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();
			
			String[][] dateList = {
					{"20210618", "062"},
					{"20210619", "063"},
					{"20210625", "068"},
					{"20210626", "069"},
					{"20210702", "074"},
					{"20210703", "075"},
					{"20210709", "080"},
					{"20210710", "081"},
					{"20210716", "086"},
					{"20210717", "087"},
					{"20210723", "092"},
					{"20210724", "093"},
					{"20210730", "098"},
					{"20210731", "099"}
			};
			
			Gson gson = new Gson();
			String currentDate = StrUtil.getCurDate();
			logger.info("CurrentDate = " + currentDate);
			
			for (String[] dateSeq : dateList) {
				String targetDate = dateSeq[0];
				String seq = dateSeq[1];
				logger.info("TargetDate = " + targetDate + ", Seq = " + seq);
				
				if (targetDate.compareTo(currentDate) < 0) {
					logger.info("date is over");
					continue;
				}
				
				String uri = "/v1/goods/21002185/playSeq/PlaySeq/" + seq + "/REMAINSEAT";
				logger.info("Url = " + BASE_URL + uri);
				String eventList = webClient.get().uri(uri).exchange().block().bodyToMono(String.class).block();
				if (eventList == null) {
					logger.error("eventList is null");
					return;
				}
				
				InterparkItem interparkItem = gson.fromJson(eventList, InterparkItem.class);
				if (interparkItem == null) {
					logger.error("interparkItem is null");
					return;
				}
				
				// 날짜 값이 안들어옴. 강제로 입력
				interparkItem.getData().setPlayDate(targetDate);
				interparkItem.getData().setPlaySeq(seq);
				
				String key = interparkItem.getData().getPlayDate();
				if (itemList.containsKey(key) == true) { // exist
					InterparkItem oldItem = itemList.get(key);
					if (isChanged(oldItem, interparkItem) == true) {
						publisher.publishEvent(new InterparkUpdatedEvent(link, interparkItem));	
					}
					
				} else { // new Item
					itemList.put(key, interparkItem);
					publisher.publishEvent(new InterparkAddedEvent(link, interparkItem));	
				}
				
				Thread.sleep(50);
			}
			
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
		
		logger.info("END");
	}

	private boolean isChanged(InterparkItem oldItem, InterparkItem newItem) {
		List<InterparkRemainSeat> oldRemainSeatList = oldItem.getData().getRemainSeat();
		List<InterparkRemainSeat> newRemainSeatList = newItem.getData().getRemainSeat();
		
		if (oldRemainSeatList.size() != newRemainSeatList.size()) {
			return true;
		}
		
		for (int i = 0; i < oldRemainSeatList.size(); i++) {
			InterparkRemainSeat oldRemainSeat = oldRemainSeatList.get(i);
			InterparkRemainSeat newRemainSeat = newRemainSeatList.get(i);

			if (oldRemainSeat.getSeatGrade().equals(newRemainSeat.getSeatGrade()) == false) {
				return true;
			}
			
			if (oldRemainSeat.getSeatGradeName().equals(newRemainSeat.getSeatGradeName()) == false) {
				return true;
			}
			
			if (oldRemainSeat.getRemainCnt().equals(newRemainSeat.getRemainCnt()) == false) {
				return true;
			}
		}
		return false;
	}
}
