package com.tistory.yangpajuice.rc.service.telegrambot;

import org.slf4j.*;
import org.springframework.stereotype.*;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.*;

import com.tistory.yangpajuice.rc.constants.*;

@Component
public class InterparkBot extends TelegramBot {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	protected String getSectId() {
		return CodeConstants.SECT_ID_INTERPARK;
	}
	
	@Override
	protected String onUpdateReceivedCustom(Update update) {
		String receivedMessage = update.getMessage().getText().trim();
		String chatId = update.getMessage().getChatId().toString();

		return "";
	}
	
	@Override
	protected ReplyKeyboardMarkup getReplyKeyboardMarkup() {
		// TODO Auto-generated method stub
		return null;
	}
}
