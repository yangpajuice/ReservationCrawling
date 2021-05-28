package com.tistory.yangpajuice.rc.listener;

import org.slf4j.*;
import org.springframework.boot.context.event.*;
import org.springframework.context.event.*;
import org.springframework.stereotype.*;

@Component
public class ApplicationListener {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@EventListener
    public void onApplicationStartedEvent(ApplicationStartedEvent event) {
        System.out.println(Thread.currentThread().toString());
        System.out.println("ApplicationStartedEvent");
        
        logger.info("ApplicationStartedEvent");
    }
	
	@EventListener
    public void onContextRefreshedEvent(ContextRefreshedEvent event) {
        System.out.println(Thread.currentThread().toString());
        System.out.println("ContextRefreshedEvent");
        
        logger.info("ContextRefreshedEvent");
    }
	
	@EventListener
    public void onContextStartedEvent(ContextStartedEvent event) {
        System.out.println(Thread.currentThread().toString());
        System.out.println("ContextStartedEvent");
        
        logger.info("ContextStartedEvent");
    }
	
	@EventListener
    public void onContextStoppedEvent(ContextStoppedEvent event) {
        System.out.println(Thread.currentThread().toString());
        System.out.println("ContextStoppedEvent");
        
        logger.info("ContextStoppedEvent");
    }
 
    @EventListener
    public void onContextClosedEvent(ContextClosedEvent event) {
        System.out.println(Thread.currentThread().toString());
        System.out.println("ContextClosedEvent");
        
        logger.info("ContextClosedEvent");
    }
}
