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
		if (receivedMessage.equals("/") == true || 
				receivedMessage.startsWith(MENU_HELP) == true) { // Help
			sendMessage += "도움말 : " + MENU_HELP + "\n";
			sendMessage += "키워드추가 : " + MENU_ADD_KEYWORD + " [keyword]" + "\n";
			sendMessage += "키워드삭제 : " + MENU_REMOVE_KEYWORD + " [keyword]" + "\n";
			sendMessage += "설정보기 : " + MENU_SHOW_ALARM + "\n";
			
		} else if (receivedMessage.startsWith(MENU_ADD_KEYWORD) == true) {
			String newKeyword = receivedMessage.split(MENU_ADD_KEYWORD)[0].trim();
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
				}
			}
			
		} else if (receivedMessage.startsWith(MENU_REMOVE_KEYWORD) == true) {
			
		} else if (receivedMessage.startsWith(MENU_SHOW_ALARM) == true) {
			ConfigParam param = new ConfigParam();
			param.setSectId(CodeConstants.SECT_ID_CLIEN);
			param.setKeyId(CodeConstants.KEY_ID_ALARM_MAINCATEGORY);
			List<ConfigItem> mainCategoryList = dbService.getConfigItemList(param);
			sendMessage = "[Main Category]" + "\n";
			if (mainCategoryList != null && mainCategoryList.size() > 0) {
				for (ConfigItem mainCategory : mainCategoryList) {
					sendMessage += mainCategory.getValue() + "\n";
				}
			} else {
				sendMessage += "N/A" + "\n";
			}
			sendMessage += "\n";
			
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
			
		} else {
			sendMessage = MSG_NOT_DEFINE;
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

	@Override
	public String getBotUsername() {
		return telegramConfig.getBotUserName();
	}

	@Override
	public String getBotToken() {
		return telegramConfig.getBotToken();
	}
}
