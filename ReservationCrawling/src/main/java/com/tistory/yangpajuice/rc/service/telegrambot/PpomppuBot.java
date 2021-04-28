package com.tistory.yangpajuice.rc.service.telegrambot;

import javax.annotation.*;

import org.slf4j.*;
import org.springframework.stereotype.*;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.*;

import com.tistory.yangpajuice.rc.constants.*;
import com.tistory.yangpajuice.rc.service.telegrambot.ClienBot.*;

@Component
public class PpomppuBot extends TelegramBot {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@PostConstruct
    private void init() {
		initConfig();
	}

	@Override
	public void onUpdateReceived(Update update) {
		String receivedMessage = update.getMessage().getText().trim();
		String chatId = update.getMessage().getChatId().toString();
		logger.info("Received Message = " + chatId + " : " + receivedMessage);

		String sendMessage = "";
		if (receivedMessage.equals(MENU_START) == true) {
			updateChatId(chatId);
			sendMessage = "ChatID ▶ " + chatId;
			
		} else if (receivedMessage.startsWith(MENU_SHOW_ALARM) == true) {
			sendMessage = getConfig(CodeConstants.SECT_ID_PPOMPPU);
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
