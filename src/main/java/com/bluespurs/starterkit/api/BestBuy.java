package com.bluespurs.starterkit.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import com.bluespurs.starterkit.data.ProductResult;
import com.bluespurs.starterkit.data.bestbuy.Product;
import com.bluespurs.starterkit.data.bestbuy.ProductResults;
import com.google.gson.Gson;

public class BestBuy implements OnlineStore {
	String apikey;
	public BestBuy (String apikey) {
		this.apikey = apikey;
	}
	
	@Override
	public List<ProductResult> searchProducts(String keyword, float min_price) {
		URL url;
		Gson gson = new Gson();
		try {
			url = new URL("https://api.bestbuy.com/v1/products(longDescription="+URLEncoder.encode(keyword, "UTF-8")+"*&salePrice>"+min_price+")" + 
					"?format=json&show=sku,name,salePrice&apiKey="+apikey+"&sort=salePrice.asc");
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.connect();
			System.out.println("res:" + conn.getResponseCode());
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			ProductResults reply = gson.fromJson(reader, ProductResults.class);
			List<ProductResult> output = new ArrayList<ProductResult>();
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
	public ProductResult getCheapestMatch(String keyword, float min_price) {
		List<ProductResult> results = searchProducts(keyword, min_price);
		if (results == null) return null;
		if (results.size() > 0) return results.get(0);
		else return null;
	}
}
