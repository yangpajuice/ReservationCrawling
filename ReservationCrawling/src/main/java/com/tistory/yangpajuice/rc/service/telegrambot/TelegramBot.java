package com.tistory.yangpajuice.rc.service.telegrambot;

import java.util.*;

import javax.annotation.*;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.telegram.telegrambots.bots.*;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.*;

import com.tistory.yangpajuice.rc.config.*;
import com.tistory.yangpajuice.rc.constants.*;
import com.tistory.yangpajuice.rc.item.*;
import com.tistory.yangpajuice.rc.param.*;
import com.tistory.yangpajuice.rc.service.*;

public abstract class TelegramBot extends TelegramLongPollingBot implements IBot {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	protected final String MENU_COMMON_START = "/start";
	protected final String MENU_SHOW_ALARM = "/ShowAlarm";
	
	protected TelegramConfig telegramConfig;
	
	@Autowired
    protected DbService dbService;
	
	@PostConstruct
    private void init() {
		initConfig();
	}

	@Override
	public String getBotUsername() {
		return telegramConfig.getBotUserName();
	}

	@Override
	public String getBotToken() {
		return telegramConfig.getBotToken();
	}
	
	protected abstract String getSectId();
	protected abstract String onUpdateReceivedCustom(Update update);
	protected abstract ReplyKeyboardMarkup getReplyKeyboardMarkup();
	
	private boolean initConfig() {
		boolean rtnValue = false;
		
		try {
			telegramConfig = new TelegramConfig();
			
			ConfigParam param = new ConfigParam();
			param.setSectId(getSectId());
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
			
			rtnValue = true;
			
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
		
		return rtnValue;
	}
	
	@Override
	public void onUpdateReceived(Update update) {
		String receivedMessage = update.getMessage().getText();
		String chatId = update.getMessage().getChatId().toString();
		logger.info("Received Message = " + chatId + " : " + receivedMessage);
		
		// common
		if (receivedMessage.equals(MENU_COMMON_START) == true) {
			updateChatId(chatId);
			String sendMessage = "ChatID ▶ " + chatId;
			sendMessage(sendMessage, chatId);
			
			return;
		}
		
		// custom
		String rtnValue = onUpdateReceivedCustom(update);
		if (rtnValue != null && rtnValue.length() > 0) {
			sendMessage(rtnValue, chatId);
		}
	}
	
	public void sendMessageToAll(String sendMessage) {
		sendMessage(sendMessage, telegramConfig.getBotChatId());
	}
	
	public void sendMessage(String sendMessage, String chatId) {
		logger.info("message = " + sendMessage + ", chatID = " + chatId);
		
		try {
			SendMessage message = new SendMessage();
			message.setText(sendMessage);
			message.setChatId(chatId);
			
			//message.setDisableWebPagePreview(true);
			ReplyKeyboardMarkup replyKeyboardMarkup = getReplyKeyboardMarkup();
			if (replyKeyboardMarkup != null) {
				message.setReplyMarkup(replyKeyboardMarkup);
			}
			
			execute(message);
			
		} catch (Exception e) {
			logger.error("exception", e);
		}
	}
	
	protected boolean updateChatId(String chatId) {
		boolean rtnValue = false;
		
		try {
			if (chatId.equals(telegramConfig.getBotChatId()) == false) {
				ConfigParam param = new ConfigParam();
				param.setSectId(CodeConstants.SECT_ID_PPOMPPU);
				param.setKeyId(CodeConstants.KEY_ID_BOT_CHAT_ID);
				List<ConfigItem> configItemList = dbService.getConfigItemList(param);
				int updatedCnt = 0;
				if (configItemList == null || configItemList.size() == 0) { // insert
					ConfigItem configItem = new ConfigItem();
					configItem.setSectId(CodeConstants.SECT_ID_PPOMPPU);
					configItem.setKeyId(CodeConstants.KEY_ID_BOT_CHAT_ID);
					configItem.setSeq(1);
					configItem.setValue(chatId);
					
					updatedCnt = dbService.insertConfigItem(configItem);
					
				} else { // update
					ConfigItem configItem = configItemList.get(0);
					configItem.setValue(chatId);
					
					updatedCnt = dbService.updateConfigItem(configItem);
				}
				
				if (updatedCnt > 0) {
					rtnValue = true;
					telegramConfig.setBotChatId(chatId);
				}
			}
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
		
		return rtnValue;
	}
	
	public String getConfig(String sectId) {
		String sendMessage = "";
		
		ConfigParam param = new ConfigParam();
		param.setSectId(CodeConstants.SECT_ID_SYSTEM);
		param.setKeyId(CodeConstants.KEY_ID_ALARM_KEYWORD);
		List<ConfigItem> systemKeywordList = dbService.getConfigItemList(param);
		sendMessage = "[System Keyword]" + "\n";
		if (systemKeywordList != null && systemKeywordList.size() > 0) {
			for (ConfigItem systemKeyword : systemKeywordList) {
				sendMessage += systemKeyword.getValue() + "\n";
			}
		} else {
			sendMessage += "N/A" + "\n";
		}
		sendMessage += "\n";
		
		param = new ConfigParam();
		param.setSectId(sectId);
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
		sendMessage += "\n";
		
		param = new ConfigParam();
		param.setSectId(sectId);
		param.setKeyId(CodeConstants.KEY_ID_URL);
		List<ConfigItem> urlList = dbService.getConfigItemList(param);
		sendMessage += "[Category]" + "\n";
		if (urlList != null && urlList.size() > 0) {
			for (ConfigItem url : urlList) {
				if (url.getValue3().equals(CodeConstants.YES) == true) {
					sendMessage += url.getValue2() + "\n";
				}
			}
		} else {
			sendMessage += "N/A" + "\n";
		}
		
		return sendMessage;
	}
}
