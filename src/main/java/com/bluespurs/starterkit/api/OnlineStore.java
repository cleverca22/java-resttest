package com.bluespurs.starterkit.api;

import java.util.List;

import com.bluespurs.starterkit.data.ProductResult;

public interface OnlineStore {
	public List<ProductResult> searchProducts(String keyword);

	public ProductResult getCheapestMatch(String keyword);
}
