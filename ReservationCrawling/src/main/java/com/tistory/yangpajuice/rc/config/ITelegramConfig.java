package com.tistory.yangpajuice.rc.config;

import java.util.*;

public interface ITelegramConfig {
	public String getBotUserName();
	public String getBotToken();
	public List<String> getBotChatId();
}
