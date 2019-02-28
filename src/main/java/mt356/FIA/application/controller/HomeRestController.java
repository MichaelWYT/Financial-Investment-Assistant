package mt356.FIA.application.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import mt356.FIA.application.domain.DataRequestOptions;
import mt356.FIA.application.service.Datasource;

/**
 * This class is a RestController that handles response/requests sent from the JavaScript.
 * Each response needs a request as a pair.
 * @author Michael
 *
 */
@RestController
public class HomeRestController {
	
	private static Datasource ds;
	private boolean change = false;
	private List<String> dataList = new ArrayList<String>();
	
	/**
	 * When the front end JavaScript makes a request for stock data, this method responds by sending that data
	 * in the correct format. The format which Highcharts accepts it.
	 * @return String in JSON format
	 */
	@RequestMapping(value="/getJson", method=RequestMethod.GET,headers="Accept=application/json")
	public String response() {
		try {
			System.out.println("Getting data from Datasource");	
			
			// Pull new data from IEX if empty JSON or options have changed.
			if (dataList.isEmpty() || change) {
				if(change) {
					dataList.clear();
				}
				ds = new Datasource(DataRequestOptions.getQuote(), DataRequestOptions.getYear());
				JSONArray temp = ds.getJSON();
				
				StringBuilder dataCell = new StringBuilder();
				
				LocalDate date = null;
				LocalDateTime ldt = null;
				ZonedDateTime zdt = null;
				
				for(int i=0;i<temp.length();i++) {
					dataCell.append("[");
					
					date = LocalDate.parse(temp.getJSONObject(i).getString("date"), DateTimeFormatter.ISO_DATE);
					ldt = LocalDateTime.of(date, LocalTime.now());
					zdt = ZonedDateTime.of(ldt, ZoneId.of("Greenwich"));
					
					dataCell.append(zdt.toInstant().toEpochMilli()+",");
					dataCell.append(temp.getJSONObject(i).get("open")+",");
					dataCell.append(temp.getJSONObject(i).get("high")+",");
					dataCell.append(temp.getJSONObject(i).get("low")+",");
					dataCell.append(temp.getJSONObject(i).get("close")+",");
					dataCell.append(temp.getJSONObject(i).get("volume"));
					dataCell.append("]");
					dataList.add(dataCell.toString());
					dataCell.setLength(0);
				}
				change = false;
				
				// Options appended to the end of the data list
				dataCell.append("["+DataRequestOptions.getQuote()+","+DataRequestOptions.getName()+"]");
				dataList.add(dataCell.toString());
			}
			return new JSONArray(dataList.toString()).toString();
		} catch (IOException | JSONException e) {
			System.out.println(e.getMessage());
		}
		return new JSONArray("[{Fail:Failed to extract Json data}]").toString();
	}
	
	/**
	 * When front end sends data to the back end via POST, this method runs to catch the data and 
	 * handles it accordingly. (Handles requests from the front end)
	 * @param jsonString 
	 * @return Currently returns nothing of interest and sends back jsonString received
	 */
	@RequestMapping(value="/postJson", method=RequestMethod.POST,headers="Accept=application/json")
	public String request(@RequestBody String jsonString) {
		int year = DataRequestOptions.getYear();
		String symbol = DataRequestOptions.getQuote();
		
		/*
		 * From button press 'Apply' this sends option changes to the back end to pull:
		 * 
		 * 	A) Different Stock
		 * 	B) Change stock options (This might be done on the front end??)
		 * 	C) Apply investment Strategy by picking up on data points
		 */
		System.out.println("Testing data sent to back end: "+jsonString.substring(1, jsonString.length()-1));
		
		// Force the data into a JSONObject as a form of validation
		JSONObject convertBack = new JSONObject(jsonString.substring(1, jsonString.length()-1));		
		int nextYear = convertBack.getInt("year");
		String nextSymbol = convertBack.getString("symbol");
		
		// Any changes requires the chart to be reloaded to put data inside.
		if(year != nextYear || !symbol.equals(nextSymbol)) {
			DataRequestOptions.setYear(nextYear);
			DataRequestOptions.setQuote(nextSymbol);
			DataRequestOptions.setName(convertBack.getString("name"));
			change = true;
		}
		return convertBack.toString();
	}
}
