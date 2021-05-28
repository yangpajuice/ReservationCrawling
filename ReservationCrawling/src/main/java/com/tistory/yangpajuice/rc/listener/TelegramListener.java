package com.tistory.yangpajuice.rc.listener;

import javax.annotation.*;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.event.*;
import org.springframework.stereotype.*;
import org.telegram.telegrambots.meta.*;
import org.telegram.telegrambots.updatesreceivers.*;

import com.tistory.yangpajuice.rc.constants.*;
import com.tistory.yangpajuice.rc.item.*;
import com.tistory.yangpajuice.rc.item.event.*;
import com.tistory.yangpajuice.rc.param.*;
import com.tistory.yangpajuice.rc.service.*;
import com.tistory.yangpajuice.rc.service.telegrambot.*;
import com.tistory.yangpajuice.rc.util.*;

@Component
public class TelegramListener {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private TelegramBotsApi telegramBotsApi = null;
	
	@Autowired
    private DbService dbService;
	
	@Autowired
	private ClienBot clienBot;
	
	@Autowired
	private PpomppuBot ppomppuBot;
	
	@Autowired
	protected Telegram telegram;
	
	private void registerTelegramBot() { // TelegramBot 설정
		try {
			telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
			
			telegramBotsApi.registerBot(clienBot);
			logger.info(clienBot.getClass().getSimpleName() + " is added.");
			
			telegramBotsApi.registerBot(ppomppuBot);
			logger.info(ppomppuBot.getClass().getSimpleName() + " is added.");
			
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
	}
	
	private boolean isMainCategoryForMessage(String sectId, WebPageItem item) {
		boolean rtnValue = false;
		
		ConfigParam param = new ConfigParam();
		param.setSectId(sectId);
		param.setKeyId(CodeConstants.KEY_ID_URL);
		java.util.List<ConfigItem> configItemList = dbService.getConfigItemList(param);
		
		for (ConfigItem configItem : configItemList) {
			if (configItem.getValue2().equals(item.getMainCategory()) == true) {
				if (configItem.getValue3().equals(CodeConstants.YES) == true) {
					rtnValue = true;
					break;
				}
			}
		}

		return rtnValue;
	}
	
	private String getMatchedKeyword(String sectId, WebPageItem item) {
		String rtnKeyword = "";
		
		ConfigParam param = new ConfigParam();
		param.setSectId(sectId);
		param.setKeyId(CodeConstants.KEY_ID_ALARM_KEYWORD);
		java.util.List<ConfigItem> keywordList = dbService.getConfigItemList(param);
		if (keywordList == null || keywordList.size() == 0) {
			
		} else {
			for (ConfigItem keyword : keywordList) {
				if (keyword.getValue() == null || keyword.getValue().length() == 0) {
					continue;
				}
				
				String subject = item.getSubject().toUpperCase();
				String kwd = keyword.getValue().toUpperCase();
				
				if (subject.contains(kwd) == true) {
					rtnKeyword = kwd;
					break;
				}
				
				if (kwd.equals(CodeConstants.ALL) == true) {
					rtnKeyword = kwd;
					break;
				}
			}
		}
		
		return rtnKeyword;
	}
	
	@PostConstruct
    private void init() {
		registerTelegramBot();
		logger.info("Initialized");
	}
	
	@EventListener
	public void onClienAddedEvent(ClienAddedEvent event) {
		logger.info("onClienAddedEvent");

		String sectId = CodeConstants.SECT_ID_CLIEN;
		WebPageItem item = event.getItem();
		if (isMainCategoryForMessage(sectId, item) == false) {
			logger.error(CodeConstants.SECT_ID_PPOMPPU + " ConfigItem 알람설정이 되지 않았습니다.");
			return;
		}

		// Sect별 키워드 확인
		String keyword = getMatchedKeyword(sectId, item);
		if (keyword == null || keyword.length() == 0) {
			keyword = getMatchedKeyword(CodeConstants.SECT_ID_SYSTEM, item);
		}
		
		if (keyword == null || keyword.length() == 0) {
			logger.error(sectId + " Keyword가 없습니다.");
			return;
		}
		
		String msg = "";
		msg = "[" + sectId + "] " + item.getMainCategory() + " * " + item.getSubCategory() + "\n";
		msg += "Keyword ▶ " + keyword + "\n" + "\n";
    	msg += item.getSubject() + "\n" + "\n";
    	msg += item.getUrl();
		
    	ppomppuBot.sendMessageToAll(msg);
	}
	
	@EventListener
	public void onPpomppuAddedEvent(PpomppuAddedEvent event) {
		logger.info("onPpomppuAddedEvent");

		String sectId = CodeConstants.SECT_ID_PPOMPPU;
		WebPageItem item = event.getItem();
		if (isMainCategoryForMessage(sectId, item) == false) {
			logger.error(CodeConstants.SECT_ID_PPOMPPU + " ConfigItem 알람설정이 되지 않았습니다.");
			return;
		}

		// Sect별 키워드 확인
		String keyword = getMatchedKeyword(sectId, item);
		if (keyword == null || keyword.length() == 0) {
			keyword = getMatchedKeyword(CodeConstants.SECT_ID_SYSTEM, item);
		}
		
		if (keyword == null || keyword.length() == 0) {
			logger.error(sectId + " Keyword가 없습니다.");
			return;
		}
		
		String msg = "";
		msg = "[" + sectId + "] " + item.getMainCategory() + " * " + item.getSubCategory() + "\n";
		msg += "Keyword ▶ " + keyword + "\n" + "\n";
    	msg += item.getSubject() + "\n" + "\n";
    	msg += item.getUrl();
		
    	ppomppuBot.sendMessageToAll(msg);
	}

	@EventListener
	public void onCgvAddedEvent(CgvAddedEvent cgvAddedEvent) {
		logger.info("onCgvAddedEvent");
		
		try {
			WebPageItem webPageItem = cgvAddedEvent.getWebPageItem();
			
			String message = "[CGV]" + CodeConstants.NEW_LINE;
			message += webPageItem.getSubject() + CodeConstants.NEW_LINE;
			message += webPageItem.getArticle() + CodeConstants.NEW_LINE;
			message += webPageItem.getUrl();
			telegram.sendMessage(CodeConstants.SECT_ID_CINEMA, message);
			
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
	}
}
