package com.otc.sdk.pos.flows.sources.server.models.response.initialize;

import android.os.Parcel;
import android.os.Parcelable;

public class Header implements Parcelable {
	private String externalId;
	private String responseMessage;
	private long transactionDate;
	private int millis;
	private String transactionId;
	private int responseCode;

	protected Header(Parcel in) {
		externalId = in.readString();
		responseMessage = in.readString();
		transactionDate = in.readLong();
		millis = in.readInt();
		transactionId = in.readString();
		responseCode = in.readInt();
	}

	public Header() {
	}

	public static final Creator<Header> CREATOR = new Creator<Header>() {
		@Override
		public Header createFromParcel(Parcel in) {
			return new Header(in);
		}

		@Override
		public Header[] newArray(int size) {
			return new Header[size];
		}
	};

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(externalId);
		dest.writeString(responseMessage);
		dest.writeLong(transactionDate);
		dest.writeInt(millis);
		dest.writeString(transactionId);
		dest.writeInt(responseCode);
	}
}
