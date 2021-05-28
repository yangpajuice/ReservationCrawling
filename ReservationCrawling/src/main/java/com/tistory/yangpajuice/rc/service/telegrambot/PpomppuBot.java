package com.tistory.yangpajuice.rc.service.telegrambot;

import org.slf4j.*;
import org.springframework.stereotype.*;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.*;

import com.tistory.yangpajuice.rc.constants.*;

@Component
public class PpomppuBot extends TelegramBot {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	protected String getSectId() {
		return CodeConstants.SECT_ID_PPOMPPU;
	}
	
	@Override
	protected String onUpdateReceivedCustom(Update update) {
		String receivedMessage = update.getMessage().getText().trim();
		String chatId = update.getMessage().getChatId().toString();
		logger.info("Received Message = " + chatId + " : " + receivedMessage);

		String sendMessage = "";
		if (receivedMessage.startsWith(MENU_SHOW_ALARM) == true) {
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
		
		return "";
	}

	@Override
	public String getBotUsername() {
		return telegramConfig.getBotUserName();
	}

	@Override
	public String getBotToken() {
		return telegramConfig.getBotToken();
	}

	@Override
	protected ReplyKeyboardMarkup getReplyKeyboardMarkup() {
		// TODO Auto-generated method stub
		return null;
	}
}
