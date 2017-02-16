package com.bluespurs.starterkit.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.HttpsURLConnection;

import com.bluespurs.starterkit.data.ProductResult;
import com.bluespurs.starterkit.data.ProductResultList;
import com.bluespurs.starterkit.data.walmart.Product;
import com.bluespurs.starterkit.data.walmart.ProductResults;
import com.google.gson.Gson;

import net.spy.memcached.MemcachedClient;
import net.spy.memcached.internal.GetFuture;

public class Walmart implements OnlineStore {
	String apikey;
	MemcachedClient cache;

	public Walmart (String apikey, MemcachedClient cache) {
		this.apikey = apikey;
		this.cache = cache;
	}
	
	@Override
	public ProductResultList searchProducts(String keyword, int min_price) {
		URL url;
		Gson gson = new Gson();
		String cache_key = keyword+":walmart:"+min_price;
		GetFuture future = cache.asyncGet(cache_key);
		ProductResultList output;
		if (future != null) {
			try {
				String cache_value = (String) future.get(50, TimeUnit.MILLISECONDS);
				if (cache_value != null) {
					System.out.println(cache_value);
					output = ProductResultList.fromJson(cache_value);
					return output;
				}
			} catch (InterruptedException | ExecutionException | TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			url = new URL("https://api.walmartlabs.com/v1/search?apiKey=" + apikey +
					"&query="+URLEncoder.encode(keyword, "UTF-8") +
					"&sort=price" +
					"&facet.range=price:[" + URLEncoder.encode(""+(int)min_price+" TO 999999", "UTF-8")+"]");
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.connect();
			System.out.println("walmart res:" + conn.getResponseCode());
			if (conn.getResponseCode() != 200) return null;
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			ProductResults reply = gson.fromJson(reader, ProductResults.class);
			output = new ProductResultList();
			System.out.println("got "+reply.items.size()+" products");
			Iterator<Product> it = reply.items.iterator();
			System.out.println("best 10 from walmart");
			while (it.hasNext()) {
				Product p = it.next();
				ProductResult p2 = new ProductResult();
				p2.productName = p.name;
				p2.bestPrice = p.salePrice;
				p2.location = "Walmart";
				System.out.println(p2);
				output.add(p2);
			}
			cache.add(cache_key, 300, output.toJson());
			return output;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProductResult getCheapestMatch(String keyword, int min_price) {
		List<ProductResult> results = searchProducts(keyword, min_price);
		if (results.size() > 0) return results.get(0);
		else return null;
	}

}
