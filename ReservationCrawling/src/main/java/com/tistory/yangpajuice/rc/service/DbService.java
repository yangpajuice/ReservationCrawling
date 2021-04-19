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
	
	@Autowired
	private ICampingMapper campingMapper;
	
	@Autowired
	private IWebPageMapper webPageMapper;
	
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
			if (campingMapper.checkTableExists() < 1) {
				campingMapper.createTable();
			}
			if (webPageMapper.checkTableExists() < 1) {
				webPageMapper.createTable();
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
	
	public int insertCampingItem(CampingItem item) {
		int rtnValue = 0;
		
		try {
			rtnValue = campingMapper.insertCampingItem(item);
			
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
		
		return rtnValue;
	}
	
	public int getMaxSeq(CampingParam param) {
		return campingMapper.getMaxSeq(param);
	}
	
	public int insertWebPageItem(WebPageItem item) {
		int rtnValue = 0;
		
		try {
			rtnValue = webPageMapper.insertWebPageItem(item);
			
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
		
		return rtnValue;
	}
	
	public String getMaxIdWebPageItem(WebPageParam param) {
		String rtnValue = "";
		
		try {
			rtnValue = webPageMapper.getMaxIdWebPageItem(param);
			
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
		
		return rtnValue;
	}
	
	public List<WebPageItem> getWebPageItemList(WebPageParam param) {
		List<WebPageItem> rtnValue = null;
		
		try {
			rtnValue = webPageMapper.getWebPageItemList(param);
			
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
		
		return rtnValue;
	}
	
	public int updateWebPageItemIdIncrease(WebPageItem item) {
		int rtnValue = 0;
		
		try {
			rtnValue = webPageMapper.updateWebPageItemIdIncrease(item);
			
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
		
		return rtnValue;
	}
	
	public int insertConfigItem(ConfigItem item) {
		int rtnValue = 0;
		
		try {
			rtnValue = configMapper.insertConfigItem(item);
			
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
		
		return rtnValue;
	}
	
	public int deleteConfigItem(ConfigItem item) {
		int rtnValue = 0;
		
		try {
			rtnValue = configMapper.deleteConfigItem(item);
			
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
		
		return rtnValue;
	}
}
