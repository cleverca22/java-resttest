package com.bluespurs.starterkit.data;

import java.util.Formatter;
import java.util.Locale;

public class ProductResult {
	public String productName;
	public float bestPrice;
	public String currency;
	public String location;
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb, Locale.US);
		String result = formatter.format("price:%s location:%s name:%s", bestPrice, location, productName).toString();
		formatter.close();
		return result;
	}
}
