package com.otc.sdk.pos.flows.sources.server.models.response.authorize;

import android.os.Parcel;
import android.os.Parcelable;

public class Merchant implements Parcelable {

	private String merchantId;
	private String merchantName;

	protected Merchant(Parcel in) {
		merchantId = in.readString();
		merchantName = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(merchantId);
		dest.writeString(merchantName);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<Merchant> CREATOR = new Creator<Merchant>() {
		@Override
		public Merchant createFromParcel(Parcel in) {
			return new Merchant(in);
		}

		@Override
		public Merchant[] newArray(int size) {
			return new Merchant[size];
		}
	};

	public void setMerchantId(String merchantId){
		this.merchantId = merchantId;
	}

	public String getMerchantId(){
		return merchantId;
	}

	public void setMerchantName(String merchantName){
		this.merchantName = merchantName;
	}

	public String getMerchantName(){
		return merchantName;
	}

	@Override
 	public String toString(){
		return 
			"Merchant{" + 
			"merchantId = '" + merchantId + '\'' + 
			",merchantName = '" + merchantName + '\'' + 
			"}";
		}
}