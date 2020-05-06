package com.tistory.yangpajuice.rc.util;

import org.slf4j.*;
import org.springframework.web.client.*;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.javascript.*;
import com.google.gson.*;

public class Utils {
	static final public Gson gson = new Gson();
    static final public RestTemplate restTemplate = new RestTemplate();
    
	public static synchronized HtmlPage getPage(String url) {
		final Logger logger = LoggerFactory.getLogger(HtmlPage.class);
		
    	WebClient webClient = null;
    	HtmlPage page = null;
    	
    	try {
    		webClient = new WebClient(BrowserVersion.CHROME);
    		webClient.setJavaScriptErrorListener(new SilentJavaScriptErrorListener());
    		webClient.getOptions().setTimeout(60000);
    		webClient.getOptions().setCssEnabled(false);
    		webClient.getOptions().setThrowExceptionOnScriptError(false);
    		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
    		
    		page = webClient.getPage(url);
    		
    		webClient.waitForBackgroundJavaScriptStartingBefore(200);
    	    webClient.waitForBackgroundJavaScript(20000);
    	    
    	} catch (Exception e) {
    		logger.error("An exception occurred!", e);
    		
    	} finally {
    		webClient.close();
    	}
    	
    	return page;
    }
}
