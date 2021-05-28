package com.tistory.yangpajuice.rc;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.scheduling.annotation.*;

import com.tistory.yangpajuice.rc.util.*;

@SpringBootApplication
@EnableScheduling
public class ReservationCrawlingApplication implements CommandLineRunner {
	private static final Logger logger = LoggerFactory.getLogger(ReservationCrawlingApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ReservationCrawlingApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		logger.info("*** ReservationCrawlingApplication is now running");
		//telegram.sendMessage("*** ReservationCrawling is now running");
	}
}
