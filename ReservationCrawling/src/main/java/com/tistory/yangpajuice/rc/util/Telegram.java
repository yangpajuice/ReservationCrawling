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
	
	public boolean sendSystemMessage(String sectId, String text) {
		String msg = "[" + sectId + "]";
		if (text != null && text.length() > 0) {
			msg += "\n" + text;
		}
		
		return sendMessage(CodeConstants.SECT_ID_SYSTEM, msg);
	}
	
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
	
	private boolean isMainCategoryForMessage(String sectId, WebPageItem item) {
		boolean rtnValue = false;
		
		ConfigParam param = new ConfigParam();
		param.setSectId(sectId);
		param.setKeyId(CodeConstants.KEY_ID_URL);
		List<ConfigItem> configItemList = dbService.getConfigItemList(param);
		
		for (ConfigItem configItem : configItemList) {
			if (configItem.getValue2().equals(item.getMainCategory()) == true) {
				if (configItem.getValue3().equals("Y") == true) {
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
			}
		}
		
		return rtnKeyword;
	}
	
	public boolean sendMessage(String sectId, WebPageItem item) {
		logger.error(sectId + " => " + item.getMainCategory() + " - " + item.getSubject());
		
		if (isMainCategoryForMessage(sectId, item) == false) {
			logger.error(sectId + " ConfigItem 알람설정이 되지 않았습니다.");
			return false;
		}

		// Sect별 키워드 확인
		String keyword = getMatchedKeyword(sectId, item);
		if (keyword == null || keyword.length() == 0) {
			keyword = getMatchedKeyword(CodeConstants.SECT_ID_SYSTEM, item);
		}
		
		if (keyword == null || keyword.length() == 0) {
			logger.error(sectId + " Keyword가 없습니다.");
			return false;
		}
		
		String msg = "";
		msg = "[" + sectId + "] " + item.getMainCategory() + " * " + item.getSubCategory() + "\n";
		msg += "Keyword ▶ " + keyword + "\n" + "\n";
    	msg += item.getSubject() + "\n" + "\n";
    	msg += item.getUrl();
		
		return sendMessage(sectId, msg);
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
