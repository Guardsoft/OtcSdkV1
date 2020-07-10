package com.otc.sdk.pos.flows.sources.server.models.request.cancel;

public class Order{
	private double amount;
	private String channel = "mpos";
	private String currency;
	private boolean countable;
	private String purchaseNumber;

	public void setAmount(double amount){
		this.amount = amount;
	}

	public double getAmount(){
		return amount;
	}

	public void setChannel(String channel){
		this.channel = channel;
	}

	public String getChannel(){
		return channel;
	}

	public void setCurrency(String currency){
		this.currency = currency;
	}

	public String getCurrency(){
		return currency;
	}

	public void setCountable(boolean countable){
		this.countable = countable;
	}

	public boolean isCountable(){
		return countable;
	}

	public void setPurchaseNumber(String purchaseNumber){
		this.purchaseNumber = purchaseNumber;
	}

	public String getPurchaseNumber(){
		return purchaseNumber;
	}

	@Override
 	public String toString(){
		return 
			"Order{" + 
			"amount = '" + amount + '\'' + 
			",channel = '" + channel + '\'' + 
			",currency = '" + currency + '\'' + 
			",countable = '" + countable + '\'' + 
			",purchaseNumber = '" + purchaseNumber + '\'' + 
			"}";
		}
}
