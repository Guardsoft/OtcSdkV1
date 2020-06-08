package com.otc.sdk.pos.flows.sources.server.models.response.initialize;

import android.os.Parcel;
import android.os.Parcelable;

public class Merchant implements Parcelable {
	private AdditionalParams additionalParams;
	private String merchantId;
	private String merchantName;

	protected Merchant(Parcel in) {
		merchantId = in.readString();
		merchantName = in.readString();
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

	public void setAdditionalParams(AdditionalParams additionalParams){
		this.additionalParams = additionalParams;
	}

	public AdditionalParams getAdditionalParams(){
		return additionalParams;
	}

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
			"additionalParams = '" + additionalParams + '\'' + 
			",merchantId = '" + merchantId + '\'' + 
			",merchantName = '" + merchantName + '\'' + 
			"}";
		}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(merchantId);
		dest.writeString(merchantName);
	}
}
