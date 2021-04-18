package com.tistory.yangpajuice.rc.util;

import java.io.*;
import java.net.*;
import java.util.*;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import com.tistory.yangpajuice.rc.config.*;
import com.tistory.yangpajuice.rc.constants.*;
import com.tistory.yangpajuice.rc.item.*;
import com.tistory.yangpajuice.rc.param.*;
import com.tistory.yangpajuice.rc.service.*;

@Component
public class Telegram {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private TelegramConfig defaultTelegramConfig;
	
	@Autowired
    private DbService dbService;
	
	public boolean sendMessage(String sectId, String text) {
		TelegramConfig telegramConfig = new TelegramConfig();
		
		ConfigParam param = new ConfigParam();
		param.setSectId(sectId);
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
		
		return sendMessage(telegramConfig, text);
	}
	
	public boolean sendMessage(ITelegramConfig config, String text) {
		boolean result = false;

		try {
			String urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";

			if (config == null) {
				config = defaultTelegramConfig;
			}
			String apiToken = config.getBotToken();
			String chatId = config.getBotChatId();
			
			String encoded = URLEncoder.encode(text, "UTF-8");

			urlString = String.format(urlString, new Object[] { apiToken, chatId, encoded });

			URL url = new URL(urlString);
			URLConnection conn = url.openConnection();
			StringBuilder sb = new StringBuilder();
			InputStream is = new BufferedInputStream(conn.getInputStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String inputLine = "";
			while ((inputLine = br.readLine()) != null) {
				sb.append(inputLine);
			}
			result = true;
			
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
		return result;
	}

	public boolean sendPhoto(String photo_url) {
		boolean result = false;

		try {
			String urlString = "https://api.telegram.org/bot%s/sendPhoto?chat_id=%s&photo=%s";

			String apiToken = defaultTelegramConfig.getBotToken();
			String chatId = defaultTelegramConfig.getBotChatId();
			String encoded = URLEncoder.encode(photo_url, "UTF-8");

			urlString = String.format(urlString, new Object[] { apiToken, chatId, encoded });

			URL url = new URL(urlString);
			URLConnection conn = url.openConnection();
			StringBuilder sb = new StringBuilder();
			InputStream is = new BufferedInputStream(conn.getInputStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String inputLine = "";
			while ((inputLine = br.readLine()) != null) {
				sb.append(inputLine);
			}
			result = true;
			
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
		return result;
	}
}
