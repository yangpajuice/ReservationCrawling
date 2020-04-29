package com.tistory.yangpajuice.rc.config;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

@Component
public class TelegramCampingAlarmConfig implements ITelegramConfig {
	@Value("${telegram.campingalarm.username}")
	private String botUserName = "";
	
	@Value("${telegram.campingalarm.token}")
	private String botToken = "";
	
	@Value("${telegram.campingalarm.chatid}")
	private String botChatId = "";

	@Override
	public String getBotUserName() {
		return botUserName;
	}

	public void setBotUserName(String botUserName) {
		this.botUserName = botUserName;
	}

	@Override
	public String getBotToken() {
		return botToken;
	}

	public void setBotToken(String botToken) {
		this.botToken = botToken;
	}

	@Override
	public String getBotChatId() {
		return botChatId;
	}

	public void setBotChatId(String botChatId) {
		this.botChatId = botChatId;
	}
}
