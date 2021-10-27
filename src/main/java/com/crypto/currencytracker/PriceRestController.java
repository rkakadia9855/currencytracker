package com.crypto.currencytracker;

import java.io.IOException;
import org.json.JSONObject;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PriceRestController {
	
	private static String coinMarketApi = "dfa211fa-b455-42c1-ac6c-288a5761cff4";
	
	CurrencyTicker btcCoinbase;
	CurrencyTicker btcBinance;
	CurrencyTicker ethCoinbase;
	CurrencyTicker ethBinance;

	public void loadInformation() {
		
	//	String uri = "https://api.binance.com/api/v3/ticker/bookTicker";
		String uri = "https://api.coinbase.com/v2/prices/BTC-USD/buy";
	//	String uri = "https://api.coinbase.com/v2/prices/BTC-USD/sell";
	//	String uri = "https://api.coinbase.com/v2/prices/ETH-USD/buy";
	//	String uri = "https://api.coinbase.com/v2/prices/ETH-USD/sell";
	    List<NameValuePair> paratmers = new ArrayList<NameValuePair>();
	  //  paratmers.add(new BasicNameValuePair("symbol","BTCUSDT"));
	//  paratmers.add(new BasicNameValuePair("symbol","ETHUSDT"));

	    try {
	    	//Gather data for btcCoinbase
	      String result = makeAPICall(uri, paratmers);
	      JSONObject jsonObject = new JSONObject(result);
	      String buyPrice = jsonObject.getJSONObject("data").getString("amount");
	      uri = "https://api.coinbase.com/v2/prices/BTC-USD/sell";
	      result = makeAPICall(uri, paratmers);
	      jsonObject = new JSONObject(result);
	      String sellPrice = jsonObject.getJSONObject("data").getString("amount");
	      btcCoinbase = new CurrencyTicker("Coinbase", "BTC", "Bitcoin", buyPrice, sellPrice);
	      
	      //Gather data for ethCoinbase
	      uri = "https://api.coinbase.com/v2/prices/ETH-USD/buy";
	      result = makeAPICall(uri, paratmers);
	      jsonObject = new JSONObject(result);
	      buyPrice = jsonObject.getJSONObject("data").getString("amount");
	      uri = "https://api.coinbase.com/v2/prices/ETH-USD/sell";
	      result = makeAPICall(uri, paratmers);
	      jsonObject = new JSONObject(result);
	      sellPrice = jsonObject.getJSONObject("data").getString("amount");
	      ethCoinbase = new CurrencyTicker("Coinbase", "ETH", "Ethereum", buyPrice, sellPrice);
	      
	      //Gather data for btcBinance
	      uri = "https://api.binance.com/api/v3/ticker/bookTicker?symbol=BTCUSDT";
	      result = makeAPICall(uri, paratmers);
	      jsonObject = new JSONObject(result);
	      buyPrice = jsonObject.getString("askPrice");
	      sellPrice = jsonObject.getString("bidPrice");
	      btcBinance = new CurrencyTicker("Binance", "BTC", "Bitcoin", buyPrice, sellPrice);
	      
	      //Gather data for ethBinance
	      uri = "https://api.binance.com/api/v3/ticker/bookTicker?symbol=ETHUSDT";
	      result = makeAPICall(uri, paratmers);
	      jsonObject = new JSONObject(result);
	      buyPrice = jsonObject.getString("askPrice");
	      sellPrice = jsonObject.getString("bidPrice");
	      ethBinance = new CurrencyTicker("Binance", "ETH", "Ethereum", buyPrice, sellPrice);
	      
	      System.out.println(btcCoinbase.toString());
	      System.out.println(btcBinance.toString());
	      System.out.println(ethCoinbase.toString());
	      System.out.println(ethBinance.toString());
	    } catch (IOException e) {
	      System.out.println("Error: cannont access content - " + e.toString());
	    } catch (URISyntaxException e) {
	      System.out.println("Error: Invalid URL " + e.toString());
	    } catch (Exception e) {
	    	System.out.println("OOPS");
	    }
	}
		
	public static String makeAPICall(String uri, List<NameValuePair> parameters)
		      throws URISyntaxException, IOException {
	    String response_content = "";
	
		URIBuilder query = new URIBuilder(uri);
		query.addParameters(parameters);
		
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet request = new HttpGet(query.build());
		
		request.setHeader(HttpHeaders.ACCEPT, "application/json");
		request.addHeader("X-CMC_PRO_API_KEY", coinMarketApi);
		
	    CloseableHttpResponse response = client.execute(request);
	
	    try {
	      System.out.println(response.getStatusLine());
	      HttpEntity entity = response.getEntity();
	      response_content = EntityUtils.toString(entity);
	      EntityUtils.consume(entity);
	    } finally {
	      response.close();
	    }
		return response_content;
	}
	
	@RequestMapping("/analyse")
	public String analyse(@RequestParam(value="symbol", defaultValue="BTC") String symbol) {
    	//return new CurrencyTicker(symbol);
		this.loadInformation();
		String analysis = "";
		if(symbol.toLowerCase().equals("btc")) {
			if(Integer.parseInt(btcCoinbase.getBuyPrice()) <= Integer.parseInt(btcBinance.getBuyPrice()) ) {
				if(Integer.parseInt(btcBinance.getSellPrice()) > Integer.parseInt(btcCoinbase.getBuyPrice())) {
					// buy coinbase sell binance
					analysis = "You can probably profit by buying your bitcoin from coinbase and selling it on binance";
				}
				else {
					// buy coinbase sell coinbase
					analysis = "You can probably profit by buying your bitcoin from coinbase and selling it on coinbase";
				}
			}
			else {
				if(Integer.parseInt(btcCoinbase.getSellPrice()) > Integer.parseInt(btcBinance.getBuyPrice())) {
					// buy binance sell coinbase
					analysis = "You can probably profit by buying your bitcoin from binance and selling it on coinbase";
				}
				else {
					// buy binance sell binance
					analysis = "You can probably profit by buying your bitcoin from binance and selling it on binance";
				}
			}
		}
		else if(symbol.toLowerCase().equals("eth")) {
			if(Integer.parseInt(ethCoinbase.getBuyPrice()) <= Integer.parseInt(ethBinance.getBuyPrice()) ) {
				if(Integer.parseInt(ethBinance.getSellPrice()) > Integer.parseInt(ethCoinbase.getBuyPrice())) {
					// buy coinbase sell binance
					analysis = "You can probably profit by buying your ethereum from coinbase and selling it on binance";
				}
				else {
					// buy coinbase sell coinbase
					analysis = "You can probably profit by buying your ethereum from coinbase and selling it on coinbase";
				}
			}
			else {
				if(Integer.parseInt(ethCoinbase.getSellPrice()) > Integer.parseInt(ethBinance.getBuyPrice())) {
					// buy binance sell coinbase
					analysis = "You can probably profit by buying your ethereum from binance and selling it on coinbase";
				}
				else {
					// buy binance sell binance
					analysis = "You can probably profit by buying your ethereum from binance and selling it on binance";
				}
			}
		}
		else {
			analysis = "Sorry, we do not have the currency you passed in our database.";
		}
		return analysis;
	}

}
