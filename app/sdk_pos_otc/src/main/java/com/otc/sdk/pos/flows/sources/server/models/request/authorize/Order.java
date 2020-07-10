package com.otc.sdk.pos.flows.sources.server.models.request.authorize;

import android.os.Parcel;
import android.os.Parcelable;

public class Order implements Parcelable {
	private double amount;
	private int installment = 0;
	private String channel = "mpos";
	private String currency;
	private boolean countable;
	private String purchaseNumber;

	public Order() {
	}

	protected Order(Parcel in) {
		amount = in.readDouble();
		installment = in.readInt();
		channel = in.readString();
		currency = in.readString();
		countable = in.readByte() != 0;
		purchaseNumber = in.readString();
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

	public void setInstallment(int installment){
		this.installment = installment;
	}

	public int getInstallment(){
		return installment;
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
			",installment = '" + installment + '\'' + 
			",channel = '" + channel + '\'' + 
			",currency = '" + currency + '\'' + 
			",countable = '" + countable + '\'' + 
			",purchaseNumber = '" + purchaseNumber + '\'' + 
			"}";
		}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(amount);
		dest.writeInt(installment);
		dest.writeString(channel);
		dest.writeString(currency);
		dest.writeByte((byte) (countable ? 1 : 0));
		dest.writeString(purchaseNumber);
	}
}
