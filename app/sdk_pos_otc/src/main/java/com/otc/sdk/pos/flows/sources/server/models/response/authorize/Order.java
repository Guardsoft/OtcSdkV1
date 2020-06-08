package com.otc.sdk.pos.flows.sources.server.models.response.authorize;

import com.google.gson.annotations.SerializedName;

public class Order{

	@SerializedName("amount")
	private double amount;

	@SerializedName("traceNumber")
	private String traceNumber;

	@SerializedName("authorizationCode")
	private String authorizationCode;

	@SerializedName("channel")
	private String channel;

	@SerializedName("automatic")
	private boolean automatic;

	@SerializedName("actionDescription")
	private String actionDescription;

	@SerializedName("transactionDate")
	private String transactionDate;

	@SerializedName("transactionId")
	private String transactionId;

	@SerializedName("authorizedAmount")
	private double authorizedAmount;

	@SerializedName("installment")
	private int installment;

	@SerializedName("currency")
	private String currency;

	@SerializedName("actionCode")
	private String actionCode;

	@SerializedName("countable")
	private boolean countable;

	@SerializedName("purchaseNumber")
	private String purchaseNumber;

	@SerializedName("status")
	private String status;

	public void setAmount(double amount){
		this.amount = amount;
	}

	public double getAmount(){
		return amount;
	}

	public void setTraceNumber(String traceNumber){
		this.traceNumber = traceNumber;
	}

	public String getTraceNumber(){
		return traceNumber;
	}

	public void setAuthorizationCode(String authorizationCode){
		this.authorizationCode = authorizationCode;
	}

	public String getAuthorizationCode(){
		return authorizationCode;
	}

	public void setChannel(String channel){
		this.channel = channel;
	}

	public String getChannel(){
		return channel;
	}

	public void setAutomatic(boolean automatic){
		this.automatic = automatic;
	}

	public boolean isAutomatic(){
		return automatic;
	}

	public void setActionDescription(String actionDescription){
		this.actionDescription = actionDescription;
	}

	public String getActionDescription(){
		return actionDescription;
	}

	public void setTransactionDate(String transactionDate){
		this.transactionDate = transactionDate;
	}

	public String getTransactionDate(){
		return transactionDate;
	}

	public void setTransactionId(String transactionId){
		this.transactionId = transactionId;
	}

	public String getTransactionId(){
		return transactionId;
	}

	public void setAuthorizedAmount(double authorizedAmount){
		this.authorizedAmount = authorizedAmount;
	}

	public double getAuthorizedAmount(){
		return authorizedAmount;
	}

	public void setInstallment(int installment){
		this.installment = installment;
	}

	public int getInstallment(){
		return installment;
	}

	public void setCurrency(String currency){
		this.currency = currency;
	}

	public String getCurrency(){
		return currency;
	}

	public void setActionCode(String actionCode){
		this.actionCode = actionCode;
	}

	public String getActionCode(){
		return actionCode;
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

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	@Override
 	public String toString(){
		return 
			"Order{" + 
			"amount = '" + amount + '\'' + 
			",traceNumber = '" + traceNumber + '\'' + 
			",authorizationCode = '" + authorizationCode + '\'' + 
			",channel = '" + channel + '\'' + 
			",automatic = '" + automatic + '\'' + 
			",actionDescription = '" + actionDescription + '\'' + 
			",transactionDate = '" + transactionDate + '\'' + 
			",transactionId = '" + transactionId + '\'' + 
			",authorizedAmount = '" + authorizedAmount + '\'' + 
			",installment = '" + installment + '\'' + 
			",currency = '" + currency + '\'' + 
			",actionCode = '" + actionCode + '\'' + 
			",countable = '" + countable + '\'' + 
			",purchaseNumber = '" + purchaseNumber + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}