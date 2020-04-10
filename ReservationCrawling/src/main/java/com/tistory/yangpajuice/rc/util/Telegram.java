package com.tistory.yangpajuice.rc.util;

import java.io.*;
import java.net.*;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import com.tistory.yangpajuice.rc.config.*;

@Component
public class Telegram {
private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private TelegramConfig telegramConfig;
	
	public boolean sendMessage(String text) {
		boolean result = false;

		try {
			String urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";

			String apiToken = telegramConfig.getBotToken();
			String chatId = telegramConfig.getBotChatId();
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

			String apiToken = telegramConfig.getBotToken();
			String chatId = telegramConfig.getBotChatId();
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
