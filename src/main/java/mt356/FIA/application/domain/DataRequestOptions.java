package mt356.FIA.application.domain;

public class DataRequestOptions {

	private static int year = 1;
	private static String symbol = "AAPL";
	private static String name = "Apple Inc.";
	
	public static String getName() {
		return name;
	}
	public static void setName(String name) {
		DataRequestOptions.name = name;
	}
	public static int getYear() {
		return year;
	}
	public static void setYear(int year) {
		DataRequestOptions.year = year;
	}
	public static String getQuote() {
		return symbol;
	}
	public static void setQuote(String quote) {
		DataRequestOptions.symbol = quote;
	}
}
