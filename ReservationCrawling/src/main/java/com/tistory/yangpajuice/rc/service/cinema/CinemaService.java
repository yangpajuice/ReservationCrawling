package com.tistory.yangpajuice.rc.service.cinema;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.*;

import com.tistory.yangpajuice.rc.service.*;
import com.tistory.yangpajuice.rc.util.*;

public abstract class CinemaService implements IService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	protected final String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36";
	
	@Autowired
	protected Telegram telegram;

	@Autowired
	protected DbService dbService;
	
	@Autowired
	protected ApplicationEventPublisher publisher;
}
