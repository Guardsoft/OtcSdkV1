package com.otc.sdk.pos.flows.sources.server.models.response.send;

public class Header{
	private String externalId;
	private String responseMessage;
	private long transactionDate;
	private int millis;
	private String transactionId;
	private int responseCode;

	public void setExternalId(String externalId){
		this.externalId = externalId;
	}

	public String getExternalId(){
		return externalId;
	}

	public void setResponseMessage(String responseMessage){
		this.responseMessage = responseMessage;
	}

	public String getResponseMessage(){
		return responseMessage;
	}

	public void setTransactionDate(long transactionDate){
		this.transactionDate = transactionDate;
	}

	public long getTransactionDate(){
		return transactionDate;
	}

	public void setMillis(int millis){
		this.millis = millis;
	}

	public int getMillis(){
		return millis;
	}

	public void setTransactionId(String transactionId){
		this.transactionId = transactionId;
	}

	public String getTransactionId(){
		return transactionId;
	}

	public void setResponseCode(int responseCode){
		this.responseCode = responseCode;
	}

	public int getResponseCode(){
		return responseCode;
	}

	@Override
 	public String toString(){
		return 
			"Header{" + 
			"externalId = '" + externalId + '\'' + 
			",responseMessage = '" + responseMessage + '\'' + 
			",transactionDate = '" + transactionDate + '\'' + 
			",millis = '" + millis + '\'' + 
			",transactionId = '" + transactionId + '\'' + 
			",responseCode = '" + responseCode + '\'' + 
			"}";
		}
}
