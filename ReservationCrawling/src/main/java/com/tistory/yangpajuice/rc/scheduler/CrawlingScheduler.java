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
	HardKernelService hardKernelService;
	
	@PostConstruct
    private void init() {
		CampingSchuduler();
		HardKernel();
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
}
