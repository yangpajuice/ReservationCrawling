package com.tistory.yangpajuice.rc.config;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

@Component
public class TelegramHardkernelAlarmConfig implements ITelegramConfig {
	@Value("${telegram.hardkernelalarm.username}")
	private String botUserName = "";
	
	@Value("${telegram.hardkernelalarm.token}")
	private String botToken = "";
	
	@Value("${telegram.hardkernelalarm.chatid}")
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
