package com.tistory.yangpajuice.rc.scheduler;

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
	private DaejeocampingService daejeocampingService;
	
	@PostConstruct
    private void init() {
		// for test ****************************
		daejeocampingService.start();
	}
	
	@Scheduled(cron = "0 0/3 * * * *") // 매35분 마다
	public void CampingSchuduler() {
		daejeocampingService.start();
	}
}
