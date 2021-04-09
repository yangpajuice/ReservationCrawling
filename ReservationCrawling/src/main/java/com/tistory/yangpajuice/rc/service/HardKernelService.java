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
public class HardKernelService implements IService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final String SITE = "HARD_KERNEL";
	private final String hardKernel = "https://www.hardkernel.com/ko/product-category/odroid-board/";

	@Autowired
	private Telegram telegram;

	@Autowired
	private TelegramHardkernelAlarmConfig telegramHardkernelAlarmConfig;

	@Autowired
	private DbService dbService;

	@PostConstruct
	private void init() {
		telegram.sendMessage(telegramHardkernelAlarmConfig, "HardKernel is initialized");
	}

	@Override
	public void start() {
		logger.info("#### HardKernelService is started ####");

		try {
			Map<String, WebPageItem> webPageItemListFromDb = getWebPageItemFromDb();
			
			ConfigParam param = new ConfigParam();
			param.setSectId(CodeConstants.SECT_ID_HARDKERNEL);
			param.setKeyId(CodeConstants.KEY_ID_URL);
			List<ConfigItem> configItemList = dbService.getConfigItemList(param);
			if (configItemList != null && configItemList.size() > 0) {
				for (ConfigItem configItem : configItemList) {
					String url = configItem.getValue();
					logger.info("URL = " + url);

					Document doc = Jsoup.connect(url).userAgent(
							"Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36")
							.referrer("http://www.google.com").get();

					Map<String, WebPageItem> webPageItemListFromWeb = getWebPageItemFromWeb(doc);
					if (webPageItemListFromWeb == null || webPageItemListFromWeb.size() == 0) {
						continue;
					}

					Iterator<String> keys = webPageItemListFromWeb.keySet().iterator();
					while (keys.hasNext()) {
						String key = keys.next();
						WebPageItem webPageItemFromWeb = webPageItemListFromWeb.get(key);
						WebPageItem webPageItemFromDb = webPageItemListFromDb.get(key);

						if (webPageItemFromDb == null) { // new item
							dbService.insertWebPageItem(webPageItemFromWeb);
							
							String msg = "New [" + webPageItemFromWeb.getMainCategory() + "] \n";
							msg += webPageItemFromWeb.getSubCategory() + "\n";
							msg += webPageItemFromWeb.getUrl();
							telegram.sendMessage(telegramHardkernelAlarmConfig, msg);

						} else { // item exist
							if (webPageItemFromWeb.getSubCategory().equals(webPageItemFromDb.getSubCategory()) == true
									&& webPageItemFromWeb.getSubject().equals(webPageItemFromDb.getSubject()) == true
									&& webPageItemFromWeb.getArticle().equals(webPageItemFromDb.getArticle()) == true) {
								// same Item

							} else { // Item is updated
								// 데이터가 변경되는 경우 기존 데이터를 덮어씌우지 않고 새로 입력한다.
								// 기존의 데이터 ID를 1씩 증가시킨다. 최신데이터 ID는 항상 0이 되도록 한다.
								dbService.updateWebPageItemIdIncrease(webPageItemFromWeb);
								dbService.insertWebPageItem(webPageItemFromWeb);
								
								// 가격이 변경되는 경우
								if (webPageItemFromWeb.getSubCategory().equals(webPageItemFromDb.getSubCategory()) == false) {
									String msg = "[" + webPageItemFromWeb.getMainCategory() + "] \n";
									msg += webPageItemFromDb.getSubCategory() + " -> " + webPageItemFromWeb.getSubCategory() + "\n";
									msg += webPageItemFromWeb.getUrl();
									telegram.sendMessage(telegramHardkernelAlarmConfig, msg);
								}
							}
						}
					}
					
					Thread.sleep(2000);
				}
			} else {
				logger.error("URL Config does not exist.");
			}
		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}
	}

	private Map<String, WebPageItem> getWebPageItemFromDb() {
		Map<String, WebPageItem> rtnValue = new TreeMap<String, WebPageItem>(Collections.reverseOrder());

		try {
			WebPageParam param = new WebPageParam();
			param.setSite(SITE);
			param.setId("0"); // ID 0이 최신 데이터

			List<WebPageItem> webPageItemList = dbService.getWebPageItemList(param);
			if (webPageItemList != null && webPageItemList.size() > 0) {
				for (WebPageItem item : webPageItemList) {
					rtnValue.put(getKey(item), item);
				}
			}

		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}

		return rtnValue;
	}

	private String getKey(WebPageItem item) {
		return item.getMainCategory();
	}

	private Map<String, WebPageItem> getWebPageItemFromWeb(Document doc) {
		Map<String, WebPageItem> rtnValue = new TreeMap<String, WebPageItem>(Collections.reverseOrder());

		try {
			if (doc == null) {
				return rtnValue;
			}

			Elements elements = doc.getElementsByClass("woocommerce-LoopProduct-link");
			for (Element elm : elements) {
				Elements subElements = elm.getElementsByAttribute("href");
				String itemUrl = subElements.attr("href");
				String name = elm.text();
				logger.info(name + " / itemUrl = " + itemUrl);

				Document subDoc = Jsoup.connect(itemUrl).userAgent(
						"Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36")
						.timeout(30000).referrer("http://www.google.com").get();

				WebPageItem webPageItem = getWebPageItem(subDoc);
				rtnValue.put(getKey(webPageItem), webPageItem);

				Thread.sleep(500);
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
			webPageItem.setId("0");
			webPageItem.setInsertedDate(StrUtil.getCurDateTime());
			webPageItem.setUrl(doc.baseUri());

			// Product Code
			Elements elmsMainCategory = doc.getElementsByClass("product-title");
			if (elmsMainCategory != null && elmsMainCategory.size() > 0) {
				String mainCategory = elmsMainCategory.get(0).text();
				webPageItem.setMainCategory(mainCategory);
			}

			// Price
			Elements elmsSubCategory = doc.getElementsByClass("product-page-price");
			if (elmsSubCategory != null && elmsSubCategory.size() > 0) {
				String subCategory = elmsSubCategory.get(0).text();
				webPageItem.setSubCategory(subCategory);
			}

			// Short Description
			Elements elmsSubject = doc.getElementsByClass("product-short-description");
			if (elmsSubject != null && elmsSubject.size() > 0) {
				String subject = elmsSubject.get(0).text();
				webPageItem.setSubject(subject);
			}

			// Description
			Elements elmsArticle = doc.getElementsByClass("woocommerce-Tabs-panel--description");
			if (elmsArticle != null && elmsArticle.size() > 0) {
				String article = elmsArticle.get(0).text();
				webPageItem.setArticle(article);
			}

		} catch (Exception e) {
			logger.error("An exception occurred!", e);
		}

		return webPageItem;
	}
}
