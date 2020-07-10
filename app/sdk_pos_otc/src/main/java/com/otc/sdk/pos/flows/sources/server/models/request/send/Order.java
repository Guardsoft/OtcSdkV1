package com.otc.sdk.pos.flows.sources.server.models.request.send;

public class Order{
	private String channel;
	private String purchaseNumber;

	public void setChannel(String channel){
		this.channel = channel;
	}

	public String getChannel(){
		return channel;
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
			"channel = '" + channel + '\'' + 
			",purchaseNumber = '" + purchaseNumber + '\'' + 
			"}";
		}
}
