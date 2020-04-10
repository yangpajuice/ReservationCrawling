package com.tistory.yangpajuice.rc.config;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

@Component
public class TelegramConfig {
	@Value("${telegram.bot.username}")
	private String botUserName = "";
	
	@Value("${telegram.bot.token}")
	private String botToken = "";
	
	@Value("${telegram.bot.chatid}")
	private String botChatId = "";

	public String getBotUserName() {
		return botUserName;
	}

	public void setBotUserName(String botUserName) {
		this.botUserName = botUserName;
	}

	public String getBotToken() {
		return botToken;
	}

	public void setBotToken(String botToken) {
		this.botToken = botToken;
	}

	public String getBotChatId() {
		return botChatId;
	}

	public void setBotChatId(String botChatId) {
		this.botChatId = botChatId;
	}
}
