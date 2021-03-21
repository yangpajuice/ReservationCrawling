package com.tistory.yangpajuice.rc.service;

import java.util.*;

import javax.annotation.*;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import com.tistory.yangpajuice.rc.item.*;
import com.tistory.yangpajuice.rc.mapper.*;
import com.tistory.yangpajuice.rc.param.*;

@Service
@Transactional
public class DbService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private IConfigMapper configMapper;
	
	@PostConstruct
    private void init() {
		createTable();
		logger.info("Initialized");
	}
	
	private boolean createTable() {
		try {
			if (configMapper.checkTableExists() < 1) {
				configMapper.createTable();
			}
			
			return true;
			
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
		
		return false;
	}
	
	public List<ConfigItem> getConfigItemList(ConfigParam param) {
		List<ConfigItem> rtnValue = null;
		
		try {
			rtnValue = configMapper.getConfigItemList(param);
			
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
		
		return rtnValue;
	}
}
