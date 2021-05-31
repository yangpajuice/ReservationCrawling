package com.tistory.yangpajuice.rc.listener;

import javax.annotation.*;
import java.util.List;

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
	protected CampingBot campingBot;
	
	@Autowired
	protected HardkernelBot hardkernelBot;
	
	@Autowired
	protected CinemaBot cinemaBot;
	
	private void registerTelegramBot() { // TelegramBot 설정
		try {
			registerTelegramBot(clienBot);
			registerTelegramBot(ppomppuBot);
			registerTelegramBot(campingBot);
			registerTelegramBot(hardkernelBot);
			registerTelegramBot(cinemaBot);

		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
	}
	
	private void registerTelegramBot(TelegramBot bot) {
		try {
			if (telegramBotsApi == null) {
				telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
			}
			
			telegramBotsApi.registerBot(bot);
			logger.info(bot.getClass().getSimpleName() + " is added.");
			
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
	}
	
	private boolean isMainCategoryForMessage(String sectId, WebPageItem item) {
		boolean rtnValue = false;
		
		ConfigParam param = new ConfigParam();
		param.setSectId(sectId);
		param.setKeyId(CodeConstants.KEY_ID_URL);
		List<ConfigItem> configItemList = dbService.getConfigItemList(param);
		
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
		List<ConfigItem> keywordList = dbService.getConfigItemList(param);
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

		try {
			String sectId = CodeConstants.SECT_ID_CLIEN;
			WebPageItem item = event.getItem();
			if (isMainCategoryForMessage(sectId, item) == false) {
				logger.error(sectId + " ConfigItem 알람설정이 되지 않았습니다.");
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
	    	
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
	}
	
	@EventListener
	public void onCampingDateAddedEvent(CampingDateAddedEvent event) {
		logger.info("onCampingDateAddedEvent");
		
		try {
			String msg = "▷ " + event.getSiteName() + " Open Date : " + event.getDateDesc();
			campingBot.sendMessageToAll(msg);
			
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
	}
	
	@EventListener
	public void onCampingAddedEvent(CampingAddedEvent event) {
		logger.info("onCampingAddedEvent");
		
		try {
			String siteName = event.getSiteName();
			String url = event.getUrl();
			String dateDesc = event.getDateDesc();
			CampingItem campingItem = event.getCampingItem();
			
			String msg = "▶ " + siteName + " " + campingItem.getState() + "\n";
			msg += "Date : " + dateDesc + "\n";
			msg += "Area : " + campingItem.getArea() + " " + campingItem.getNo() + "\n";
			msg += "\n";
			
			msg += url;
			
			ConfigParam param = new ConfigParam();
			param.setSectId(CodeConstants.SECT_ID_CAMP);
			param.setKeyId(CodeConstants.KEY_ID_TELEGRAM);
			List<ConfigItem> configItemList = dbService.getConfigItemList(param);
			if (configItemList != null && configItemList.size() > 0) {
				for (ConfigItem configItem : configItemList) {
					if (configItem.getValue().equals(siteName) == true) {
						if (configItem.getValue2().equals(CodeConstants.YES) == true) {
							campingBot.sendMessageToAll(msg);
							break;
						}
					}
				}
			}
			
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
	}
	
	@EventListener
	public void onHardkernelAddedEvent(HardkernelAddedEvent event) {
		logger.info("onHardkernelAddedEvent");
		
		try {
			WebPageItem webPageItemFromWeb = event.getWebPageItem();
			String msg = "New [" + webPageItemFromWeb.getMainCategory() + "] \n";
			msg += webPageItemFromWeb.getSubCategory() + "\n";
			msg += webPageItemFromWeb.getUrl();
	
			hardkernelBot.sendMessageToAll(msg);
			
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
	}
	
	@EventListener
	public void onHardkernelChangedEvent(HardkernelChangedEvent event) {
		logger.info("onHardkernelChangedEvent");
		
		try {
			WebPageItem webPageItemFromWeb = event.getWebPageItemFromWeb();
			WebPageItem webPageItemFromDb = event.getWebPageItemFromDb();
			
			// 가격이 변경되는 경우
			if (webPageItemFromWeb.getSubCategory().equals(webPageItemFromDb.getSubCategory()) == false) {
				String msg = "[" + webPageItemFromWeb.getMainCategory() + "] \n";
				msg += webPageItemFromDb.getSubCategory() + " -> " + webPageItemFromWeb.getSubCategory() + "\n";
				msg += webPageItemFromWeb.getUrl();
				
				hardkernelBot.sendMessageToAll(msg);
			}
			
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
	}
	
	@EventListener
	public void onPpomppuAddedEvent(PpomppuAddedEvent event) {
		logger.info("onPpomppuAddedEvent");

		try {
			String sectId = CodeConstants.SECT_ID_PPOMPPU;
			WebPageItem item = event.getItem();
			if (isMainCategoryForMessage(sectId, item) == false) {
				logger.error(sectId + " ConfigItem 알람설정이 되지 않았습니다.");
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
	    	
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
	}

	@EventListener
	public void onCgvAddedEvent(CgvAddedEvent cgvAddedEvent) {
		logger.info("onCgvAddedEvent");
		
		try {
			WebPageItem webPageItem = cgvAddedEvent.getWebPageItem();
			String sectId = CodeConstants.SECT_ID_CINEMA;
			String keyword = getMatchedKeyword(sectId, webPageItem);
			if (keyword == null || keyword.length() == 0) {
				logger.error(sectId + " Keyword가 없습니다.");
				return;
			}
			
			String message = "▶ " + sectId + " Keyword : " + keyword + CodeConstants.NEW_LINE;
			message += CodeConstants.NEW_LINE;
			message += "[CGV] " + webPageItem.getSubCategory() + CodeConstants.NEW_LINE;
			message += webPageItem.getSubject() + CodeConstants.NEW_LINE;
			message += webPageItem.getArticle() + CodeConstants.NEW_LINE;
			message += webPageItem.getUrl();
			
			cinemaBot.sendMessageToAll(message);
			
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
	}
	
	@EventListener
	public void onLotteCinemaAddedEvent(LotteCinemaAddedEvent event) {
		logger.info("onLotteCinemaAddedEvent");
		
		try {
			WebPageItem webPageItem = event.getWebPageItem();
			String sectId = CodeConstants.SECT_ID_CINEMA;
			String keyword = getMatchedKeyword(sectId, webPageItem);
			if (keyword == null || keyword.length() == 0) {
				logger.error(sectId + " Keyword가 없습니다.");
				return;
			}
			
			// send message
			String message = "▶ " + sectId + " Keyword : " + keyword + CodeConstants.NEW_LINE;
			message += CodeConstants.NEW_LINE;
			message += "[롯데시네마]" + CodeConstants.NEW_LINE;
			message += webPageItem.getSubject() + CodeConstants.NEW_LINE;
			message += webPageItem.getArticle() + CodeConstants.NEW_LINE;
			message += CodeConstants.NEW_LINE;
			message += webPageItem.getUrl();
			
			cinemaBot.sendMessageToAll(message);
			
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
	}
}
