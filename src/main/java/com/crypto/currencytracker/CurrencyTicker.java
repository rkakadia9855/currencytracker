package com.crypto.currencytracker;

public class CurrencyTicker {
	
	private String exchange;
	private String symbol;
	private String name;
	private String buyPrice;
	private String sellPrice;
	
	public CurrencyTicker(String exchange, String symbol, String name, String buyPrice,
			String sellPrice) {
		this.exchange = exchange;
		this.symbol = symbol; 
		this.name = name;
		this.buyPrice = buyPrice;
		this.sellPrice = sellPrice;
	}
	
	public String getExchange() {
		return this.exchange;
	}
	public String getSymbol() {
		return this.symbol;
	}
	public String getName() {
		return this.name;
	}
	public String getBuyPrice() {
		return this.buyPrice;
	}
	public String getSellPrice() {
		return this.sellPrice;
	}
	
	public String toString() {
		return this.exchange+", "+this.symbol+", "+this.name+", "+this.buyPrice+", "+this.sellPrice;
	}
	
}
