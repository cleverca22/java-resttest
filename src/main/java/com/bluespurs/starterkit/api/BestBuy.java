package com.bluespurs.starterkit.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import com.bluespurs.starterkit.data.ProductResult;
import com.bluespurs.starterkit.data.ProductResultList;
import com.bluespurs.starterkit.data.bestbuy.Product;
import com.bluespurs.starterkit.data.bestbuy.ProductResults;
import com.google.gson.Gson;

import net.spy.memcached.MemcachedClient;

public class BestBuy implements OnlineStore {
	String apikey;
	MemcachedClient cache;

	public BestBuy (String apikey, MemcachedClient cache) {
		this.apikey = apikey;
		this.cache = cache;
	}
	
	@Override
	public List<ProductResult> searchProducts(String keyword, int min_price) {
		URL url;
		Gson gson = new Gson();
		String cache_key = keyword+":bestbuy:"+min_price;
		String cache_value = (String) cache.get(cache_key);
		ProductResultList output;
		if (cache_value != null) {
			output = ProductResultList.fromJson(cache_value);
			return output;
		}
		try {
			url = new URL("https://api.bestbuy.com/v1/products(longDescription="+URLEncoder.encode(keyword, "UTF-8")+"*&salePrice>"+min_price+")" + 
					"?format=json&show=sku,name,salePrice&apiKey="+apikey+"&sort=salePrice.asc");
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.connect();
			System.out.println("bestbuy res:" + conn.getResponseCode());
			if (conn.getResponseCode() != 200) return null;
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			ProductResults reply = gson.fromJson(reader, ProductResults.class);
			output = new ProductResultList();
			System.out.println("got "+reply.products.size()+" products");
			Iterator<Product> it = reply.products.iterator();
			System.out.println("best 10 from bestbuy");
			while (it.hasNext()) {
				Product p = it.next();
				ProductResult p2 = new ProductResult();
				p2.productName = p.name;
				p2.bestPrice = p.salePrice;
				p2.location = "BestBuy";
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
		if (results == null) return null;
		if (results.size() > 0) return results.get(0);
		else return null;
	}
}
