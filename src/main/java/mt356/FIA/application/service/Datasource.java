package mt356.FIA.application.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.util.Arrays;
import org.json.JSONArray;
import org.json.JSONException;

public class Datasource {

	/**
	 * API Key for Alphavantage.
	 * private static final String KEY = "J3R3H5N2NP31H3QC";
	 */
	
	private static JSONArray jsonArray;
	private static List<String[]> symbolPairs = new ArrayList<String[]>();
	
	// Data provided for free by IEX. View IEX’s Terms of Use.
	
	public Datasource(){}
	
	/**
	 * Constructor calls initial data point starting at 1 year worth of data. Default call is for 1 months worth of data.
	 * Connection will will timeout if no connection is made to the datasource, or the no data is being read. (Both 3 seconds).
	 * 
	 * IEX is a datasource that allows you to specify the data range from date A to date B
	 * 
	 * @param stock : Symbol of the stock to pull i.e. AAPL for Apple Inc.
	 * @param year : How many years of data to pull ranging from 1-5 (Includes every single day of trading)
	 * @throws IOException : When connection fails throws IOException
	 * @throws JSONException : When data cannot fit into JSON format throw JSONException 
	 */
	public Datasource(String stock, int year) throws IOException, JSONException{
		callStockData(stock, year);
	}
	
	/**
	 * This calls the IEX server to and puts the data into the JSONArray
	 * @param stock : Symbol of the stock to pull i.e. AAPL for Apple Inc.
	 * @param year : How many years of data to pull ranging from 1-5 (Includes every single day of trading)
	 * @throws IOException : When connection fails throws IOException
	 * @throws JSONException : When data cannot fit into JSON format throw JSONException
	 */
	public void callStockData(String stock, int year) throws IOException, JSONException {
		URL url = new URL("https://api.iextrading.com/1.0/stock/"+stock+"/chart/"+year+"y");
		URLConnection conn = url.openConnection();
		conn.setConnectTimeout(3000);
		conn.setReadTimeout(3000);
		
		InputStreamReader inStream = new InputStreamReader(conn.getInputStream());
		BufferedReader br = new BufferedReader(inStream);
		
		String input;
		StringBuilder response = new StringBuilder();
		
		while((input = br.readLine()) != null) {
			response.append(input);
		}
		
		br.close();
		
		jsonArray = new JSONArray(response.toString());
	}
	
	/**
	 * Returns peers (related company symbols) in relation to the symbol provided i.e. AAPL = Technology
	 * so other tech companies would be pulled.
	 * @param symbol : Initial Symbol to find similar symbols related to its sector
	 * @return String[] : 
	 * @throws IOException
	 */
	public String[] getPeers(String symbol) throws IOException {
		URL url = new URL("https://api.iextrading.com/1.0/stock/"+symbol+"/peers");
		URLConnection conn = url.openConnection();
		conn.setConnectTimeout(3000);
		conn.setReadTimeout(3000);
		
		InputStreamReader inStream = new InputStreamReader(conn.getInputStream());
		BufferedReader br = new BufferedReader(inStream);
		
		String input;
		StringBuilder response = new StringBuilder();
		
		while((input = br.readLine()) != null) {
			response.append(input);
		}
		
		br.close();
		
		return response.toString().substring(1, response.length()-1).split(",");
	}
	
	public JSONArray getSymbols() throws IOException {
		URL url = new URL("https://api.iextrading.com/1.0/ref-data/symbols");
		URLConnection conn = url.openConnection();
		conn.setConnectTimeout(3000);
		conn.setReadTimeout(3000);
		
		InputStreamReader inStream = new InputStreamReader(conn.getInputStream());
		BufferedReader br = new BufferedReader(inStream);
		
		String input;
		StringBuilder response = new StringBuilder();
		
		while((input = br.readLine()) != null) {
			response.append(input);
		}
		
		br.close();
		
		return new JSONArray(response.toString());
	}
	
	public List<String[]> getStockList() throws IOException {
		
		Comparator<Object> c = new Comparator<Object>() {
			@Override
			public int compare(Object symbols, Object peer) {
				return ((HashMap<Object, Object>) symbols).get("symbol").toString().compareTo(peer.toString());
			}
		};

		if(symbolPairs.isEmpty()) {			
			// Default peer is AAPL, this can change if parameter is changed.
			String[] peers = getPeers("AAPL");
			List<Object> symbols = getSymbols().toList();
			
			String[] pair = new String[2];
			pair[0] = "AAPL";
			pair[1] = "Apple Inc.";
			
			symbolPairs.add(pair);
			
			StringBuilder peerFormatted = new StringBuilder();
			
			for(String peer: peers) {
				peerFormatted.append(peer.substring(1, peer.length()-1));
				int index = Collections.binarySearch(symbols,peerFormatted.toString(), c);
				if(index != -1) {
					pair = new String[2];
					pair[0] = peerFormatted.toString();
					pair[1] = ((HashMap<Object,Object>) symbols.get(index)).get("name").toString(); 
					symbolPairs.add(pair);
				}
				peerFormatted.setLength(0);
			}
		}
		
		return symbolPairs;
	}
	
	public JSONArray getJSON() {
		return jsonArray;
	}
	
}
