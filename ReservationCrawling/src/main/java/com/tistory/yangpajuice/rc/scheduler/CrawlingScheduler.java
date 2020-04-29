package com.tistory.yangpajuice.rc.scheduler;

import java.util.*;

import javax.annotation.*;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.*;

import com.tistory.yangpajuice.rc.service.*;

@Component
public class CrawlingScheduler {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private List<CampingService> campingServiceList;
	
	@Autowired
	private HardKernelService hardKernelService;
	
	@Autowired
	private ClienService clienService;
	
	@PostConstruct
    private void init() {
		CampingSchuduler();
		HardKernel();
		Clien();
	}
	
	@Scheduled(cron = "0 0/3 * * * *") // 매3분 마다
	public void CampingSchuduler() {
		for (IService campingService : campingServiceList) {
			campingService.start();
		}
	}
	
	@Scheduled(cron = "30 3/10 08-17 ? * MON-FRI")
	public void HardKernel() {
		hardKernelService.start();
	}
	
	@Scheduled(cron = "0 0/2 08-18 ? * MON-FRI") // 월~금, 매월, 아무 날이나, 08:00 ~ 20:59, 2분마다, 0초에
	public void Clien() {
		clienService.start();
	}
}
