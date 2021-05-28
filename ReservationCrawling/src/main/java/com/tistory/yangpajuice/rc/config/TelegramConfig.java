package com.tistory.yangpajuice.rc.config;

import java.util.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

@Component
public class TelegramConfig implements ITelegramConfig {
	private String botUserName = "";
	
	private String botToken = "";

	private List<String> botChatId = null;

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

	public List<String> getBotChatId() {
		return botChatId;
	}

	public void setBotChatId(List<String> botChatId) {
		this.botChatId = botChatId;
	}
}
