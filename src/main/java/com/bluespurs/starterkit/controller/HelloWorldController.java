package com.bluespurs.starterkit.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.google.gson.Gson;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.BinaryConnectionFactory;
import net.spy.memcached.MemcachedClient;

import com.bluespurs.starterkit.api.BestBuy;
import com.bluespurs.starterkit.api.OnlineStore;
import com.bluespurs.starterkit.api.Walmart;
import com.bluespurs.starterkit.data.ProductResult;

@RestController
public class HelloWorldController {
    public static final String INTRO = "The Bluespurs Interview Starter Kit is running properly.";
    public static final Logger log = LoggerFactory.getLogger(HelloWorldController.class);
    private List<OnlineStore> stores;
    private MemcachedClient cache;
    
    public HelloWorldController() {
		try {
			cache = new MemcachedClient(new BinaryConnectionFactory(), AddrUtil.getAddresses("127.0.0.1:11211"));
		} catch (IOException e) {
			// if it can't connect to memcache, leave cache as null
			e.printStackTrace();
		}
    	stores = new ArrayList<OnlineStore>();
    	stores.add(new BestBuy("pfe9fpy68yg28hvvma49sc89", cache));
    	stores.add(new Walmart("rm25tyum3p9jm9x9x7zxshfa", cache));
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
    public String productSearch(@RequestParam(value="name", required=true) String keyword,
    		@RequestParam(value="min_price", required=false) String min_price) {
    	int real_min_price;
    	if (min_price != null) real_min_price = Integer.parseInt(min_price);
    	else real_min_price = 0;
    	Gson gson = new Gson();
    	ProductResult result = null;
    	
    	Iterator<OnlineStore> it = stores.iterator();
    	while (it.hasNext()) {
    		OnlineStore store = it.next();
    		long start = System.currentTimeMillis();
    		ProductResult result2 = store.getCheapestMatch(keyword, real_min_price);
    		long stop = System.currentTimeMillis();
    		System.out.println("scan took:" + (stop - start));
    		if (result == null) {
    			result = result2;
    		} else if (result2.bestPrice < result.bestPrice) {
    			result = result2;
    		}
    	}
    	return gson.toJson(result);
    }
}
