package com.smc.wheredmymoneygo.currency;

public class Currency {
	
	private String _id;
	private String country;
	private float conversionRate; //multiply by this number to get value in base currency
	private String symbol;
	private String timeStamp;
	
	public Currency(String id, float conversionRate, String country, String symbol, String timeStamp) {
		 this.set_id(id);
		 this.setCountry(country);
		 this.setConversionRate(conversionRate);
		 this.setSymbol(symbol);
		 this.setTimeStamp(timeStamp);
		
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public float getConversionRate() {
		return conversionRate;
	}

	public void setConversionRate(float conversionRate) {
		this.conversionRate = conversionRate;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}


	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	
}
