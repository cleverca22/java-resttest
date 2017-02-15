package com.bluespurs.starterkit.api;

import java.util.List;

import com.bluespurs.starterkit.data.ProductResult;

public interface OnlineStore {
	public List<ProductResult> searchProducts(String keyword, float min_price);

	public ProductResult getCheapestMatch(String keyword, float min_price);
}
