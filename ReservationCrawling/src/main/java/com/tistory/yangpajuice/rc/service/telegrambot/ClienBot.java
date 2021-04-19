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
	private final String MENU_ADD_ALARM = "/AddAlarm";
	private final String MENU_REMOVE_ALARM = "/RemoveAlarm";
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
				receivedMessage.toUpperCase().equals(MENU_HELP) == true) { // Help
			sendMessage += "도움말 : " + MENU_HELP + "\n";
			sendMessage += "알람추가 : " + MENU_ADD_ALARM + "\n";
			sendMessage += "알람삭제 : " + MENU_REMOVE_ALARM + "\n";
			sendMessage += "알람보기 : " + MENU_SHOW_ALARM + "\n";
			
		} else if (receivedMessage.toUpperCase().equals(MENU_ADD_ALARM) == true) {
			
		} else if (receivedMessage.toUpperCase().equals(MENU_REMOVE_ALARM) == true) {
			
		} else if (receivedMessage.toUpperCase().equals(MENU_SHOW_ALARM) == true) {
			
		} else {
			sendMessage = MSG_NOT_DEFINE;
		}
		
		SendMessage message = new SendMessage();
		message.setText(sendMessage);
		message.setChatId(chatId);
		
		try {
			execute(message);
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
