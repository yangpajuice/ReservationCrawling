package com.tistory.yangpajuice.rc.service;

import java.util.*;

import javax.annotation.*;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.*;
import org.springframework.stereotype.*;

import com.tistory.yangpajuice.rc.constants.*;
import com.tistory.yangpajuice.rc.item.*;
import com.tistory.yangpajuice.rc.item.event.*;
import com.tistory.yangpajuice.rc.param.*;
import com.tistory.yangpajuice.rc.util.*;

@Service
public class PpomppuService implements IService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final String SITE = "Ppomppu";
	
	@Autowired
    private DbService dbService;
	
	@Autowired
	protected ApplicationEventPublisher publisher;
	
	@PostConstruct
    private void init() {
		logger.info("Initialized");
	}
	
	@Override
	public void start() {
		logger.info("#### PpomppuService is started #### ");
		
		try {
			ArrayList<WebPageItem> items = getPpomppuItems();
			for (WebPageItem item : items) {
				//telegram.sendMessage(CodeConstants.SECT_ID_PPOMPPU, item);
				publisher.publishEvent(new PpomppuAddedEvent(item));
				Thread.sleep(100);
			}
			
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
	}
	
	private ArrayList<WebPageItem> getPpomppuItems() {
		ArrayList<WebPageItem> rtnValue = new ArrayList<WebPageItem>();
		
		try {
			ConfigParam param = new ConfigParam();
			param.setSectId(CodeConstants.SECT_ID_PPOMPPU);
			param.setKeyId(CodeConstants.KEY_ID_URL);
			List<ConfigItem> configItemList = dbService.getConfigItemList(param);
			if (configItemList != null && configItemList.size() > 0) {
				for (ConfigItem configItem : configItemList) {
					String url = configItem.getValue();
					String mainCategory = configItem.getValue2();
					logger.info(mainCategory + " URL = " + url);
					
					Document doc = Jsoup.connect(url).userAgent(
							"Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36")
							.timeout(30000)
							.referrer("http://www.google.com").get();
					
					
					ArrayList<WebPageItem> items = getPpomppuItems(mainCategory, doc);
					rtnValue.addAll(items);
				}
			}
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
		
		return rtnValue;
	}
	
	private ArrayList<WebPageItem> getPpomppuItems(String mainCategory, Document doc) {
		ArrayList<WebPageItem> rtnValue = new ArrayList<WebPageItem>();
		
		try {
			if (doc == null) {
				return rtnValue;
			}
			
			WebPageParam param = new WebPageParam();
			param.setMainCategory(mainCategory);
			String curMaxId = dbService.getMaxIdWebPageItem(param);
			logger.info("Max ID = " + curMaxId);
			
			Elements elements = doc.getElementsByClass("bbsList_new");
			elements = elements.get(0).getElementsByClass("none-border");
			for (Element elm : elements) {
				String baseUrl = elm.baseUri();
				baseUrl = baseUrl.split("bbs_list")[0];
				
				Elements subElements = elm.getElementsByAttribute("href");
				String subUrl = subElements.attr("href");
				
				String fullUrl = baseUrl + subUrl;
				logger.info("fullUrl = " + fullUrl);
				Document subDoc = Jsoup.connect(fullUrl).userAgent(
						"Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36")
						.timeout(30000)
						.referrer("http://www.google.com").get();
				
				WebPageItem webPageItem = getWebPageItem(mainCategory, subDoc);
				
				String newId = webPageItem.getId();
				if (curMaxId != null && newId.compareTo(curMaxId) <= 0) {
					break;
				}

				dbService.insertWebPageItem(webPageItem);
				rtnValue.add(webPageItem);
				
				Thread.sleep(500);
			}
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
		
		return rtnValue;
	}
	
	private WebPageItem getWebPageItem(String mainCategory, Document doc) {
		WebPageItem webPageItem = new WebPageItem();
		
		try {
			webPageItem.setSite(SITE);
			webPageItem.setInsertedDate(StrUtil.getCurDateTime());
			webPageItem.setMainCategory(mainCategory);
			
			Elements elms = doc.select("meta");
			for (Element elm : elms) {
				String property = elm.attr("property");
				if (property.equals("og:url") == true) {
					String url = elm.attr("content");
					webPageItem.setUrl(url);
					
					String tmp[] = url.split("no=");
					webPageItem.setId(tmp[1]);
					
				} else if (property.equals("og:title") == true) {
					String title = elm.attr("content");
					webPageItem.setSubject(title);
				}
			}
			
			Elements elmsSubCategory = doc.getElementsByClass("hi");
			if (elmsSubCategory != null && elmsSubCategory.size() > 0) {
				String tmp = elmsSubCategory.get(0).text(); // ex) 식품/건강 | 2021-04-22 11:05
				String[] tmpList = tmp.split("\\|");
				
				for (int i = 0; i < tmpList.length; i++) {
					String tmpItem = tmpList[i].trim();
					
					if (i == (tmpList.length - 1)) { // 마지막은 날짜 ex) 2021-04-22 11:05
						String postDate = tmpItem;
						postDate = StrUtil.toDateFormat("yyyy-MM-dd HH:mm", "yyyyMMddHHmmss", postDate);
						webPageItem.setPostDate(postDate);
						
					} else {
						String subCategory = tmpItem;
						if (webPageItem.getSubCategory() == null || webPageItem.getSubCategory().length() == 0) {
							webPageItem.setSubCategory(subCategory);
						} else {
							webPageItem.setSubCategory(webPageItem.getSubCategory() + " | " + subCategory);
						}
					}
				}
			}

			Element elmArticle = doc.getElementById("KH_Content");
			String article = elmArticle.text();
			webPageItem.setArticle(article);
			
			Elements elmsWriter = doc.getElementsByClass("info");
			if (elmsWriter != null) {
				String userId = elmsWriter.get(0).child(0).text();
				webPageItem.setUserId(userId);
			}
			
			Elements elmsLink = doc.getElementsByClass("noeffect");
			if (elmsLink != null && elmsLink.size() > 0) {
				String link = elmsLink.get(0).attr("href");
				webPageItem.setLink(link);
			}
			
//			webPageItem.setLink("");
			
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
		
		return webPageItem;
	}
}
