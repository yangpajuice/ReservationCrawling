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
import com.tistory.yangpajuice.rc.service.cinema.*;
import com.tistory.yangpajuice.rc.service.telegrambot.*;

@Component
public class CrawlingScheduler {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	TelegramBotsApi telegramBotsApi = null;

	@Autowired
	private List<CampingService> campingServiceList;
	
	@Autowired
	private HardKernelService hardKernelService;
	
	@Autowired
	private ClienService clienService;
	
	@Autowired
	private PpomppuService ppomppuService;
	
	@Autowired
	private LotteCinemaService lotteCinemaService;
	
	@Autowired
	private CgvCinemaService cgvCinemaService;

	@PostConstruct
    private void init() {
		
		// for test ***************************
		//cgvCinemaService.start();

//		CampingSchuduler();
//		Ppomppu();
//		HardKernel();
//		Clien();
	}
	
	@Scheduled(fixedRate = 1000 * 60) // 1분
	public void CinemaScheduler() {
		lotteCinemaService.start();
		cgvCinemaService.start();
	}
	
	@Scheduled(fixedRate = 1000 * 30) // 매30초 마다
	public void CampingSchuduler() {
		logger.info("CampingSchuduler START");
		
		for (IService campingService : campingServiceList) {
			campingService.start();
		}
		
		logger.info("CampingSchuduler END");
	}
	
	@Scheduled(fixedRate = 1000 * 60 * 60) // 60분마다
	public void HardKernel() {
		hardKernelService.start();
	}
	
	@Scheduled(fixedRate = 1000 * 60 * 2) // 2분마다
	public void Clien() {
		clienService.start();
	}
	
	@Scheduled(fixedRate = 1000 * 60 * 2) // 2분마다
	public void Ppomppu() {
		ppomppuService.start();
	}
}
