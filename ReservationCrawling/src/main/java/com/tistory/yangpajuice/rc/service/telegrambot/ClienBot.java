package com.tistory.yangpajuice.rc.service.telegrambot;

import java.util.*;

import javax.annotation.*;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.telegram.telegrambots.bots.*;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.*;

import com.tistory.yangpajuice.rc.config.*;
import com.tistory.yangpajuice.rc.constants.*;
import com.tistory.yangpajuice.rc.item.*;
import com.tistory.yangpajuice.rc.param.*;
import com.tistory.yangpajuice.rc.service.*;

@Component
public class ClienBot extends TelegramLongPollingBot {
	public enum ClienMode {
		NONE,
		ADD_KEYWORD,
		REMOVE_KEYWORD
	}
	
	private ClienMode mode = ClienMode.NONE;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final String MENU_HELP = "/HELP";
	private final String MENU_ADD_KEYWORD = "/AddKeyword";
	private final String MENU_REMOVE_KEYWORD = "/RemoveKeyWord";
	private final String MENU_SHOW_ALARM = "/ShowAlarm";
	private final String MSG_NOT_DEFINE = "무슨 말씀이신지 모르겠어요";
	
	private TelegramConfig telegramConfig;
	
	@Autowired
    private DbService dbService;

	@PostConstruct
    private void init() {
		telegramConfig = new TelegramConfig();
		
		ConfigParam param = new ConfigParam();
		param.setSectId(CodeConstants.SECT_ID_CLIEN);
		List<ConfigItem> configItemList = dbService.getConfigItemList(param);
		for (ConfigItem configItem : configItemList) {
			if (configItem.getKeyId().equals(CodeConstants.KEY_ID_BOT_CHAT_ID) == true) {
				telegramConfig.setBotChatId(configItem.getValue());
				
			} else if (configItem.getKeyId().equals(CodeConstants.KEY_ID_BOT_TOKEN) == true) {
				telegramConfig.setBotToken(configItem.getValue());
				
			} else if (configItem.getKeyId().equals(CodeConstants.KEY_ID_BOT_USER_NAME) == true) {
				telegramConfig.setBotUserName(configItem.getValue());
			}
		}
	}

	@Override
	public void onUpdateReceived(Update update) {
		String receivedMessage = update.getMessage().getText();
		String chatId = update.getMessage().getChatId().toString();
		logger.info("Received Message = " + chatId + " : " + receivedMessage);
		
		String sendMessage = "";
		if (receivedMessage.startsWith("/") == true) { // 메뉴 처리
			if (receivedMessage.equals("/") == true || receivedMessage.startsWith(MENU_HELP) == true) { // Help
				mode = ClienMode.NONE;
				sendMessage = getHelpMessage();
				
			} else if (receivedMessage.startsWith(MENU_ADD_KEYWORD) == true) {
				mode = ClienMode.ADD_KEYWORD;
				sendMessage = "추가할 키워드는 무엇입니까?";
				
			} else if (receivedMessage.startsWith(MENU_REMOVE_KEYWORD) == true) {
				mode = ClienMode.REMOVE_KEYWORD;
				sendMessage = "삭제할 키워드는 무엇입니까?";
				
			} else if (receivedMessage.startsWith(MENU_SHOW_ALARM) == true) {
				mode = ClienMode.NONE;
				sendMessage = getConfig();
				
			} else {
				mode = ClienMode.NONE;
				sendMessage = MSG_NOT_DEFINE;
			}
			
		} else { // 처리
			if (mode.equals(ClienMode.ADD_KEYWORD) == true) {
				String newKeyword = receivedMessage;
				if (newKeyword == null || newKeyword.length() == 0) {
					sendMessage = "키워드를 입력해 주세요." + "\n";
					
				} else {
					ConfigParam param = new ConfigParam();
					param.setSectId(CodeConstants.SECT_ID_CLIEN);
					param.setKeyId(CodeConstants.KEY_ID_ALARM_KEYWORD);
					List<ConfigItem> keywordList = dbService.getConfigItemList(param);
					int maxSeq = 1;
					boolean existItem = false;
					
					if (keywordList != null && keywordList.size() > 0) {
						for (ConfigItem keyword : keywordList) {
							if (maxSeq < keyword.getSeq()) {
								maxSeq = keyword.getSeq();
							}
							
							if (newKeyword.toUpperCase().equals(keyword.getValue().toUpperCase()) == true) {
								existItem = true;
							}
						}
					}
					
					if (existItem == true) {
						sendMessage = "키워드가 이미 있습니다." + "\n";
						
					} else {
						ConfigItem newConfigItem = new ConfigItem();
						newConfigItem.setSeq(maxSeq + 1);
						newConfigItem.setSectId(CodeConstants.SECT_ID_CLIEN);
						newConfigItem.setKeyId(CodeConstants.KEY_ID_ALARM_KEYWORD);
						newConfigItem.setValue(newKeyword);
						
						dbService.insertConfigItem(newConfigItem);
						sendMessage = newKeyword + " 이(가) 추가되었습니다." + "\n";
					}
				}
			} else if (mode.equals(ClienMode.REMOVE_KEYWORD) == true) {
				String newKeyword = receivedMessage;
				if (newKeyword == null || newKeyword.length() == 0) {
					sendMessage = "키워드를 입력해 주세요." + "\n";
					
				} else {
					ConfigParam param = new ConfigParam();
					param.setSectId(CodeConstants.SECT_ID_CLIEN);
					param.setKeyId(CodeConstants.KEY_ID_ALARM_KEYWORD);
					List<ConfigItem> keywordList = dbService.getConfigItemList(param);
					ConfigItem keywordExistItem = null;
					
					if (keywordList != null && keywordList.size() > 0) {
						for (ConfigItem keyword : keywordList) {
							if (newKeyword.toUpperCase().equals(keyword.getValue().toUpperCase()) == true) {
								keywordExistItem = keyword;
								break;
							}
						}
					}
					
					if (keywordExistItem == null) {
						sendMessage = "삭제 할 키워드가 없습니다." + "\n";
						
					} else {
						dbService.deleteConfigItem(keywordExistItem);
						sendMessage = newKeyword + " 이(가) 삭제되었습니다." + "\n";
					}
				}
			}
			
			mode = ClienMode.NONE;
		}
		
		SendMessage message = new SendMessage();
		message.setText(sendMessage);
		message.setChatId(chatId);
		
		try {
			execute(message);
			logger.info("message = " + message);
		} catch (Exception e) {
			
		}
	}
	
	public String getHelpMessage() {
		String sendMessage = "";
		
		sendMessage += "도움말 : " + MENU_HELP + "\n";
		sendMessage += "키워드추가 : " + MENU_ADD_KEYWORD + "\n";
		sendMessage += "키워드삭제 : " + MENU_REMOVE_KEYWORD + "\n";
		sendMessage += "설정보기 : " + MENU_SHOW_ALARM + "\n";
		
		return sendMessage;
	}
	
	public String getConfig() {
		String sendMessage = "";
		
		ConfigParam param = new ConfigParam();
//		param.setSectId(CodeConstants.SECT_ID_CLIEN);
//		param.setKeyId(CodeConstants.KEY_ID_ALARM_MAINCATEGORY);
//		List<ConfigItem> mainCategoryList = dbService.getConfigItemList(param);
//		sendMessage = "[Main Category]" + "\n";
//		if (mainCategoryList != null && mainCategoryList.size() > 0) {
//			for (ConfigItem mainCategory : mainCategoryList) {
//				sendMessage += mainCategory.getValue() + "\n";
//			}
//		} else {
//			sendMessage += "N/A" + "\n";
//		}
//		sendMessage += "\n";
		
		param = new ConfigParam();
		param.setSectId(CodeConstants.SECT_ID_CLIEN);
		param.setKeyId(CodeConstants.KEY_ID_ALARM_KEYWORD);
		List<ConfigItem> keywordList = dbService.getConfigItemList(param);
		sendMessage += "[Keyword]" + "\n";
		if (keywordList != null && keywordList.size() > 0) {
			for (ConfigItem keyword : keywordList) {
				sendMessage += keyword.getValue() + "\n";
			}
		} else {
			sendMessage += "N/A" + "\n";
		}
		
		return sendMessage;
	}

	@Override
	public String getBotUsername() {
		return telegramConfig.getBotUserName();
	}

	@Override
	public String getBotToken() {
		return telegramConfig.getBotToken();
	}
}
