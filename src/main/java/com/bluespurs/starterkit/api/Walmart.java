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
import com.bluespurs.starterkit.data.walmart.Product;
import com.bluespurs.starterkit.data.walmart.ProductResults;
import com.google.gson.Gson;

public class Walmart implements OnlineStore {
	String apikey;
	public Walmart (String apikey) {
		this.apikey = apikey;
	}
	
	@Override
	public List<ProductResult> searchProducts(String keyword, float min_price) {
		URL url;
		Gson gson = new Gson();
		try {
			url = new URL("https://api.walmartlabs.com/v1/search?apiKey=" + apikey +
					"&query="+URLEncoder.encode(keyword, "UTF-8") +
					"&sort=price" +
					"&facet.range=price:[" + URLEncoder.encode(""+(int)min_price+" TO 999999", "UTF-8")+"]");
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.connect();
			System.out.println("res:" + conn.getResponseCode());
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			ProductResults reply = gson.fromJson(reader, ProductResults.class);
			List<ProductResult> output = new ArrayList<ProductResult>();
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
		if (results.size() > 0) return results.get(0);
		else return null;
	}

}
