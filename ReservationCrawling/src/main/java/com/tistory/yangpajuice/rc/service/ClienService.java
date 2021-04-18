package com.tistory.yangpajuice.rc.service;

import java.util.*;

import javax.annotation.*;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import com.tistory.yangpajuice.rc.config.*;
import com.tistory.yangpajuice.rc.constants.*;
import com.tistory.yangpajuice.rc.item.*;
import com.tistory.yangpajuice.rc.param.*;
import com.tistory.yangpajuice.rc.util.*;

@Service
public class ClienService implements IService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final String SITE = "Clien";
	private final String BASE_URL = "https://www.clien.net";
	private final String RULE_SUBURL = "/service/board/rule/";
	
	@Autowired
	private Telegram telegram;
	
	@Autowired
    private DbService dbService;
	
	private Map<String, ClienEventItem> prevItems;
	
	@PostConstruct
    private void init() {
		prevItems = getClienItems();
		Iterator<String> keys = prevItems.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            ClienEventItem value = prevItems.get(key);
            logger.info("ClienService.init() PrevItem = " + value.getDesc());
        }
        
        telegram.sendMessage(CodeConstants.SECT_ID_CLIEN, "Clien is initialized");
	}

	@Override
	public void start() {
		logger.info("#### ClienService is started #### ");
		
		try {
			Map<String, ClienEventItem> clienItems = getClienItems();
			
			Iterator<String> keys = clienItems.keySet().iterator();
	        while (keys.hasNext()) {
	            String key = keys.next();
	            ClienEventItem value = clienItems.get(key);
	        	if (prevItems.containsKey(key) == true) {
	        		continue;
	        	}
	        	prevItems.put(key, value);
	        	
	        	String msg = "[Clien]\n";
	        	msg += value.getDesc() + "\n";
	        	msg += value.getUrl();
	        	telegram.sendMessage(CodeConstants.SECT_ID_CLIEN, msg);
	        	Thread.sleep(100);
	        }
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
	}
	
	private Map<String, ClienEventItem> getClienItems() {
		Map<String, ClienEventItem> rtnValue = new TreeMap<String, ClienEventItem>(Collections.reverseOrder());
		
		try {
			ConfigParam param = new ConfigParam();
			param.setSectId(CodeConstants.SECT_ID_CLIEN);
			param.setKeyId(CodeConstants.KEY_ID_URL);
			List<ConfigItem> configItemList = dbService.getConfigItemList(param);
			if (configItemList != null && configItemList.size() > 0) {
				for (ConfigItem configItem : configItemList) {
					String url = configItem.getValue();
					logger.info("URL = " + url);
					
					Document doc = Jsoup.connect(url).userAgent(
							"Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36")
							.timeout(30000)
							.referrer("http://www.google.com").get();
					
					
					Map<String, ClienEventItem> items = getClienItems(url, doc);
					rtnValue.putAll(items);
				}
			}
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
		
		return rtnValue;
	}
	
	private Map<String, ClienEventItem> getClienItems(String url, Document doc) {
		Map<String, ClienEventItem> rtnValue = new TreeMap<String, ClienEventItem>(Collections.reverseOrder());
		
		try {
			if (doc == null) {
				return rtnValue;
			}
			
			WebPageParam param = new WebPageParam();
			param.setUrl(url);
			String curMaxId = dbService.getMaxIdWebPageItem(param);
			logger.info("Max ID = " + curMaxId);
			
			Elements elements = doc.getElementsByClass("list_subject");
			for (Element elm : elements) {
				Elements subElements = elm.getElementsByAttribute("href");
				String subUrl = subElements.attr("href");
				if (subUrl.startsWith(RULE_SUBURL) == true) {
					continue;
				}
				subElements = elm.getElementsByAttribute("title");
				if (subElements == null || subElements.size() == 0) {
					continue;
				}
				logger.info("Sub URL = " + subUrl);
				
				String fullUrl = BASE_URL + subUrl;
				Document subDoc = Jsoup.connect(fullUrl).userAgent(
						"Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36")
						.timeout(30000)
						.referrer("http://www.google.com").get();
				
				WebPageItem webPageItem = getWebPageItem(subDoc);
				String newId = webPageItem.getId();
				if (curMaxId != null && newId.compareTo(curMaxId) <= 0) {
					break;
				}

				dbService.insertWebPageItem(webPageItem);
				
				Thread.sleep(1000);
			}
			
			String mainCategory = doc.selectFirst("div h2").text();
			logger.info("Main Category = " + mainCategory);
			
			Elements elms = doc.select("span.list_subject");
			for (Element elm : elms) {
				String subject = elm.attr("title");
				
				Element subElm = elm.selectFirst("a");
				String subUrl = subElm.attr("href");
				
				ClienEventItem clienEventItem = new ClienEventItem();
				clienEventItem.setId(subUrl);
				clienEventItem.setDesc(subject);
				clienEventItem.setUrl(BASE_URL + subUrl);
				
				rtnValue.put(clienEventItem.getId(), clienEventItem);
			}
			
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
		
		return rtnValue;
	}
	
	private WebPageItem getWebPageItem(Document doc) {
		WebPageItem webPageItem = new WebPageItem();
		
		try {
			webPageItem.setSite(SITE);
			webPageItem.setInsertedDate(StrUtil.getCurDateTime());
			
			Elements elms = doc.select("meta");
			for (Element elm : elms) {
				String property = elm.attr("property");
				if (property.equals("url") == true) {
					String url = elm.attr("content");
					webPageItem.setUrl(url);
				}
			}

			elms = doc.getElementsByClass("post_category");
			if (elms != null && elms.size() > 0) {
				String subCategory = elms.get(0).text();
				webPageItem.setSubCategory(subCategory);
			}
			
			Element elmMainCategory = doc.getElementById("boardName");
			String mainCategory = elmMainCategory.attr("value");
			webPageItem.setMainCategory(mainCategory);
			
			Element elmSubject = doc.getElementById("subject");
			String subject = elmSubject.attr("value");
			webPageItem.setSubject(subject);
			
			Element elmArticle = doc.getElementById("content");
			String article = elmArticle.attr("value");
			webPageItem.setArticle(article);
			
			Element elmWriter = doc.getElementById("writer");
			String userId = elmWriter.attr("value");
			webPageItem.setUserId(userId);
			
			Element elmBoardSn = doc.getElementById("boardSn");
			String id = elmBoardSn.attr("value");
			webPageItem.setId(id);
			
//			webPageItem.setLink("");
			
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
		
		return webPageItem;
	}
}
