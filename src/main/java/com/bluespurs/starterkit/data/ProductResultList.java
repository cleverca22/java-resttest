package com.bluespurs.starterkit.data;

import java.util.ArrayList;
import com.google.gson.Gson;

public class ProductResultList extends ArrayList<ProductResult> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String toJson() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

	public static ProductResultList fromJson(String cache_value) {
		Gson gson = new Gson();
		return gson.fromJson(cache_value, ProductResultList.class);
	}
}
