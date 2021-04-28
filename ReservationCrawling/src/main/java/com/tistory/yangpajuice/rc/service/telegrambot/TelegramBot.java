package com.tistory.yangpajuice.rc.service.telegrambot;

import java.util.*;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.telegram.telegrambots.bots.*;

import com.tistory.yangpajuice.rc.config.*;
import com.tistory.yangpajuice.rc.constants.*;
import com.tistory.yangpajuice.rc.item.*;
import com.tistory.yangpajuice.rc.param.*;
import com.tistory.yangpajuice.rc.service.*;

public abstract class TelegramBot extends TelegramLongPollingBot implements IBot {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	protected final String MENU_START = "/start"; 
	
	protected TelegramConfig telegramConfig;
	
	@Autowired
    protected DbService dbService;
	
	protected boolean initConfig() {
		boolean rtnValue = false;
		
		try {
			telegramConfig = new TelegramConfig();
			
			ConfigParam param = new ConfigParam();
			param.setSectId(CodeConstants.SECT_ID_PPOMPPU);
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
}
