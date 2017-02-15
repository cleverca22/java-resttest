package com.bluespurs.starterkit.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.google.gson.Gson;
import com.bluespurs.starterkit.api.BestBuy;
import com.bluespurs.starterkit.api.OnlineStore;
import com.bluespurs.starterkit.data.ProductResult;

@RestController
public class HelloWorldController {
    public static final String INTRO = "The Bluespurs Interview Starter Kit is running properly.";
    public static final Logger log = LoggerFactory.getLogger(HelloWorldController.class);
    private List<OnlineStore> stores;
    
    public HelloWorldController() {
    	stores = new ArrayList<OnlineStore>();
    	stores.add(new BestBuy("pfe9fpy68yg28hvvma49sc89"));
    }

    /**
     * The index page returns a simple String message to indicate if everything is working properly.
     * The method is mapped to "/" as a GET request.
     */
    @RequestMapping("/")
    public String helloWorld() {
        log.info("Visiting index page");
        return INTRO;
    }
    
    @RequestMapping("/product/search")
    public String productSearch(@RequestParam(value="name", required=true) String keyword) {
    	Gson gson = new Gson();
    	ProductResult result = null;
    	
    	Iterator<OnlineStore> it = stores.iterator();
    	while (it.hasNext()) {
    		OnlineStore store = it.next();
    		ProductResult result2 = store.getCheapestMatch(keyword);
    		if (result == null) result = result2;
    	}
    	return gson.toJson(result);
    }
}
