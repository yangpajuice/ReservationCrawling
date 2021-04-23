package com.tistory.yangpajuice.rc.scheduler;

import java.util.*;

import javax.annotation.*;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.*;
import org.telegram.telegrambots.meta.*;
import org.telegram.telegrambots.updatesreceivers.*;

import com.tistory.yangpajuice.rc.service.*;
import com.tistory.yangpajuice.rc.service.telegrambot.*;

@Component
public class CrawlingScheduler {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	TelegramBotsApi telegramBotsApi = null;

	@Autowired
	private List<TelegramBot> telegramBotList;

	@Autowired
	private List<CampingService> campingServiceList;
	
	@Autowired
	private HardKernelService hardKernelService;
	
	@Autowired
	private ClienService clienService;
	
	@Autowired
	private PpomppuService ppomppuService;

	@PostConstruct
    private void init() {
		registerTelegramBot();
		
		Ppomppu();
		CampingSchuduler();
		HardKernel();
		Clien();
	}
	
	private void registerTelegramBot() { // TelegramBot 설정
		
		try {
			telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);

			for (TelegramBot telegramBot : telegramBotList) {
				telegramBotsApi.registerBot(telegramBot);
				logger.info(telegramBot.getClass().getSimpleName() + " is added.");
			}
			
			
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
	}
	
	@Scheduled(cron = "*/30 * * * * *") // 매30초 마다
	public void CampingSchuduler() {
		logger.info("CampingSchuduler START");
		
		for (IService campingService : campingServiceList) {
			campingService.start();
		}
		
		logger.info("CampingSchuduler END");
	}
	
	@Scheduled(cron = "30 3/10 08-17 ? * MON-FRI")
	public void HardKernel() {
		hardKernelService.start();
	}
	
	@Scheduled(cron = "0 0/2 * * * *") // 2분마다, 0초에
	public void Clien() {
		clienService.start();
	}
	
	@Scheduled(cron = "0 0/2 * * * *") // 2분마다, 0초에
	public void Ppomppu() {
		ppomppuService.start();
	}
}
