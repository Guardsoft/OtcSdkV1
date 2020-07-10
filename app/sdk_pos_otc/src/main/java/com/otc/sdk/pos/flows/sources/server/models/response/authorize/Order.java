package com.otc.sdk.pos.flows.sources.server.models.response.authorize;


import android.os.Parcel;
import android.os.Parcelable;

public class Order implements Parcelable {

	private double amount;
	private String traceNumber;
	private String authorizationCode;
	private String channel;
	private boolean automatic;
	private String actionDescription;
	private String transactionDate;
	private String transactionId;
	private double authorizedAmount;
	private int installment;
	private String currency;
	private String actionCode;
	private boolean countable;
	private String purchaseNumber;
	private String status;

	protected Order(Parcel in) {
		amount = in.readDouble();
		traceNumber = in.readString();
		authorizationCode = in.readString();
		channel = in.readString();
		automatic = in.readByte() != 0;
		actionDescription = in.readString();
		transactionDate = in.readString();
		transactionId = in.readString();
		authorizedAmount = in.readDouble();
		installment = in.readInt();
		currency = in.readString();
		actionCode = in.readString();
		countable = in.readByte() != 0;
		purchaseNumber = in.readString();
		status = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(amount);
		dest.writeString(traceNumber);
		dest.writeString(authorizationCode);
		dest.writeString(channel);
		dest.writeByte((byte) (automatic ? 1 : 0));
		dest.writeString(actionDescription);
		dest.writeString(transactionDate);
		dest.writeString(transactionId);
		dest.writeDouble(authorizedAmount);
		dest.writeInt(installment);
		dest.writeString(currency);
		dest.writeString(actionCode);
		dest.writeByte((byte) (countable ? 1 : 0));
		dest.writeString(purchaseNumber);
		dest.writeString(status);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<Order> CREATOR = new Creator<Order>() {
		@Override
		public Order createFromParcel(Parcel in) {
			return new Order(in);
		}

		@Override
		public Order[] newArray(int size) {
			return new Order[size];
		}
	};

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